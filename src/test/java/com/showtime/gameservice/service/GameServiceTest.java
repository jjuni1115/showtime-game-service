package com.showtime.gameservice.service;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.entity.UserInfo;
import com.showtime.gameservice.kafka.GameEvnetProducer;
import com.showtime.gameservice.repository.GameRepository;
import com.showtime.gameservice.type.GameErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class GameServiceTest {

    GameService gameService;


    @Mock
    GameRepository gameRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    MongoTemplate mongoTemplate;

    @Test
    @DisplayName("모집 게임 종료 테스트")
    void closeGameTest() {

        //열려있는 게임이 정상적으로 닫히는지 테스트, 이미 닫힌 게임이면 오류, 등록자가 아니면 오류
        //given
        UserInfo userInfo = new UserInfo().builder()
                .userEmail("jjuni1115")
                .userId(11L)
                .build();

        Game game = Game.builder()
                .createUser(userInfo)
                .closeYn(false)
                .build();

        ResponseDto<UserInfo> userResponse = new ResponseDto<>();
        userResponse.setData(new UserInfo());
        userResponse.getData().setUserEmail("jjuni1115");
        userResponse.getData().setUserId(11L);

        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(gameRepository.closeGame(game)).willReturn(game);
        given(userServiceClient.getLoingUserInfo()).willReturn(userResponse);


        //when
        Game gameEntity = gameService.closeGame("testGameId");


        //then

        assertTrue(game.getCloseYn());


    }

    @Test
    @DisplayName("존재하지 않는 게임 오류")
    void notFoundGameTest() {
        //given
        given(gameRepository.findGame(any())).willReturn(Optional.empty());

        //when, then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> gameService.closeGame("testGameID"));
        assertEquals(GameErrorCode.GAME_NOT_FOUND.getMessage(), exception.getErrorCode().getMessage());


    }

    @Test
    @DisplayName("게임 수정 권한 테스트")
    void updateGameExceptionTest() {

        //given

        UserInfo userInfo = new UserInfo().builder()
                .userEmail("createUserId")
                .userId(11L)
                .build();

        Game game = Game.builder()
                .createUser(userInfo)
                .closeYn(false)
                .build();
        ResponseDto<UserInfo> userResponse = new ResponseDto<>();
        userResponse.setData(new UserInfo());
        userResponse.getData().setUserEmail("otherUserId");
        userResponse.getData().setUserId(12L);

        //when
        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(userServiceClient.getLoingUserInfo()).willReturn(userResponse);

        //then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> gameService.closeGame(""));
        assertEquals(GameErrorCode.CLOSE_NOT_ALLOWED.getMessage(), exception.getErrorCode().getMessage());

    }

    @Test
    @DisplayName("이미 마감된 게임 오류 테스트")
    void alreadyCloseTest() {
        //given
        Game game = Game.builder()
                .closeYn(true)
                .build();

        given(gameRepository.findGame(any())).willReturn(Optional.of(game));

        //when, then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class, () -> gameService.closeGame("testGameId"));
        assertEquals(GameErrorCode.GAME_ALREADY_CLOSE.getMessage(), exception.getErrorCode().getMessage());


    }

    @Test
    @DisplayName("게임 참가 승인 테스트")
    void playerConfirmTest() {


        //given
        UserInfo userInfo = new UserInfo().builder()
                .userEmail("createUser")
                .userId(11L)
                .build();



        List<UserInfo> waitingPlayers = new ArrayList<>(Arrays.asList(
                UserInfo.builder().userId(12L).userName("Tester1").build(),
                UserInfo.builder().userId(13L).userName("Tester2").build()
        ));


        List<UserInfo> playerList = new ArrayList<>();
        Game game = Game.builder()
                .closeYn(false)
                .createUser(userInfo)
                .waitingPlayers(waitingPlayers)
                .players(playerList)
                .build();

        ResponseDto<UserInfo> userResponse = new ResponseDto<>();
        userResponse.setData(new UserInfo());
        userResponse.getData().setUserEmail("createUser");

        ResponseDto<UserInfo> targetUserResponse = new ResponseDto<>();
        targetUserResponse.setData(new UserInfo());
        targetUserResponse.getData().setUserName("Tester1");
        targetUserResponse.getData().setUserId(12L);



        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(gameRepository.playerConfirm(any())).willReturn(game);
        given(userServiceClient.getLoingUserInfo()).willReturn(userResponse);
        given(userServiceClient.getUserInfo(12L)).willReturn(targetUserResponse);


        //when
        Game gameResult = gameService.entryConfirm("", 12L);


        //then
        assertEquals(
                List.of(UserInfo.builder().userId(13L).userName("Tester2").build()),
                gameResult.getWaitingPlayers()
        );

        assertEquals(
                List.of(UserInfo.builder().userId(12L).userName("Tester1").build()),
                gameResult.getPlayers()
        );


    }

    @Test
    @DisplayName("게임 참가 승인 오류 상황 테스트")
    void playerConfirmException() {

        //given

        UserInfo userInfo = new UserInfo().builder()
                .userEmail("createUserId")
                .userId(11L)
                .build();

        Game closeGame = Game.builder()
                .closeYn(true)
                .createUser(userInfo)
                .build();

        Game nonCreateUserGame = Game.builder()
                .createUser(userInfo)
                .closeYn(false)
                .build();

        ResponseDto<UserInfo> userResponse = new ResponseDto<>();
        userResponse.setData(new UserInfo());
        userResponse.getData().setUserEmail("anotherUserId");

        given(gameRepository.findGame("notfoundgame")).willReturn(Optional.empty());
        given(gameRepository.findGame("alreadyclosegame")).willReturn(Optional.of(closeGame));
        given(gameRepository.findGame("notcreateuser")).willReturn(Optional.of(nonCreateUserGame));
        given(userServiceClient.getLoingUserInfo()).willReturn(userResponse);

        //when, then

        CustomRuntimeException notFoundException = assertThrows(CustomRuntimeException.class, () -> gameService.entryConfirm("notfoundgame", 12L));
        CustomRuntimeException alreadyCloseException = assertThrows(CustomRuntimeException.class, () -> gameService.entryConfirm("alreadyclosegame", 12L));
        CustomRuntimeException notCreateUserException = assertThrows(CustomRuntimeException.class, () -> gameService.entryConfirm("notcreateuser", 12L));

        assertEquals(GameErrorCode.GAME_NOT_FOUND.getMessage(), notFoundException.getErrorCode().getMessage());
        assertEquals(GameErrorCode.GAME_ALREADY_CLOSE.getMessage(), alreadyCloseException.getErrorCode().getMessage());
        assertEquals(GameErrorCode.CONFIRM_NOT_ALLOWED.getMessage(), notCreateUserException.getErrorCode().getMessage());


    }

    @Test
    @DisplayName("게임 참가자 삭제 테스트")
    void deletePlayerTest() {

        //given
        UserInfo userInfo = new UserInfo().builder()
                .userEmail("createUser")
                .userId(11L)
                .build();

        List<UserInfo> players = new ArrayList<>(Arrays.asList(
                UserInfo.builder().userId(12L).userName("Tester1").build(),
                UserInfo.builder().userId(13L).userName("Tester2").build()
        ));

        Game game = Game.builder()
                .closeYn(false)
                .createUser(userInfo)
                .players(players)
                .build();

        ResponseDto<UserInfo> userResponse = new ResponseDto<>();
        userResponse.setData(new UserInfo());
        userResponse.getData().setUserEmail("createUser");

        ResponseDto<UserInfo> targetUserResponse = new ResponseDto<>();
        targetUserResponse.setData(new UserInfo());
        targetUserResponse.getData().setUserName("Tester1");
        targetUserResponse.getData().setUserId(12L);

        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(gameRepository.deletePlayer(any())).willReturn(game);
        given(userServiceClient.getLoingUserInfo()).willReturn(userResponse);
        given(userServiceClient.getUserInfo(12L)).willReturn(targetUserResponse);

        //when
        Game gameResult = gameService.deletePlayer("", 12L);

        //then
        assertEquals(
                List.of(UserInfo.builder().userId(13L).userName("Tester2").build()),
                gameResult.getPlayers()
        );

    }


    @Mock
    GameEvnetProducer gameEvnetProducer;

    @BeforeEach
    void setGameService() {
        gameService = new GameService(mongoTemplate, gameRepository, userServiceClient, gameEvnetProducer);
    }


}