package com.showtime.gameservice.service;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.entity.Game;
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
    void closeGameTest(){

        //열려있는 게임이 정상적으로 닫히는지 테스트, 이미 닫힌 게임이면 오류, 등록자가 아니면 오류
        //given
        Game game = Game.builder()
                .createUserId("jjuni1115")
                .closeYn(false)
                .build();

        ResponseDto<String> userResponse = new ResponseDto<>();
        userResponse.setData("jjuni1115");

        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(gameRepository.closeGame(game)).willReturn(game);
        given(userServiceClient.getUserId()).willReturn(userResponse);



        //when
        Game gameEntity = gameService.closeGame("testGameId");


        //then

        assertTrue(game.getCloseYn());


    }

    @Test
    @DisplayName("존재하지 않는 게임 오류")
    void notFoundGameTest(){
        //given
        given(gameRepository.findGame(any())).willReturn(Optional.empty());

        //when, then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,()->gameService.closeGame("testGameID"));
        assertEquals(GameErrorCode.GAME_NOT_FOUND.getMessage(),exception.getErrorCode().getMessage());


    }

    @Test
    @DisplayName("게임 수정 권한 테스트")
    void updateGameExceptionTest(){

        //given
        Game game = Game.builder()
                .createUserId("createUserId")
                .closeYn(false)
                .build();
        ResponseDto<String> userResponse = new ResponseDto<>();
        userResponse.setData("otherUserId");

        //when
        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(userServiceClient.getUserId()).willReturn(userResponse);

        //then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,()->gameService.closeGame(""));
        assertEquals(GameErrorCode.CLOSE_NOT_ALLOWED.getMessage(),exception.getErrorCode().getMessage());

    }

    @Test
    @DisplayName("이미 마감된 게임 오류 테스트")
    void alreadyCloseTest(){
        //given
        Game game = Game.builder()
                .closeYn(true)
                .build();

        given(gameRepository.findGame(any())).willReturn(Optional.of(game));

        //when, then
        CustomRuntimeException exception = assertThrows(CustomRuntimeException.class,()->gameService.closeGame("testGameId"));
        assertEquals(GameErrorCode.GAME_ALREADY_CLOSE.getMessage(),exception.getErrorCode().getMessage());


    }

    @Test
    @DisplayName("게임 참가 승인 테스트")
    void playerConfirmTest(){


        //given
        List<String> waitingPlayer = new ArrayList<>(Arrays.asList("testId1","testId2"));
        List<String> playerList = new ArrayList<>();
        Game game = Game.builder()
                .closeYn(false)
                .createUserId("createUser")
                .waitingPlayers(waitingPlayer)
                .players(playerList)
                .build();

        ResponseDto<String> userResponse = new ResponseDto<>();
        userResponse.setData("createUser");


        given(gameRepository.findGame(any())).willReturn(Optional.of(game));
        given(gameRepository.playerConfirm(any())).willReturn(game);
        given(userServiceClient.getUserId()).willReturn(userResponse);


        //when
        Game gameResult = gameService.entryConfirm("","testId1");


        //then
        assertEquals(List.of("testId2"),gameResult.getWaitingPlayers());
        assertEquals(List.of("testId1"),gameResult.getPlayers());



    }

    @Test
    @DisplayName("게임 참가 승인 오류 상황 테스트")
    void playerConfirmException(){

        //given
        Game closeGame = Game.builder()
                .closeYn(true)
                .createUserId("createUserId")
                .build();

        Game nonCreateUserGame = Game.builder()
                .createUserId("createUserId")
                .closeYn(false)
                .build();

        ResponseDto<String> userResponse = new ResponseDto<>();
        userResponse.setData("anotherUserId");

        given(gameRepository.findGame("notfoundgame")).willReturn(Optional.empty());
        given(gameRepository.findGame("alreadyclosegame")).willReturn(Optional.of(closeGame));
        given(gameRepository.findGame("notcreateuser")).willReturn(Optional.of(nonCreateUserGame));
        given(userServiceClient.getUserId()).willReturn(userResponse);

        //when, then

        CustomRuntimeException notFoundException = assertThrows(CustomRuntimeException.class,()->gameService.entryConfirm("notfoundgame","playerId"));
        CustomRuntimeException alreadyCloseException = assertThrows(CustomRuntimeException.class,()->gameService.entryConfirm("alreadyclosegame","playerId"));
        CustomRuntimeException notCreateUserException = assertThrows(CustomRuntimeException.class,()->gameService.entryConfirm("notcreateuser","playerId"));

        assertEquals(GameErrorCode.GAME_NOT_FOUND.getMessage(),notFoundException.getErrorCode().getMessage());
        assertEquals(GameErrorCode.GAME_ALREADY_CLOSE.getMessage(),alreadyCloseException.getErrorCode().getMessage());
        assertEquals(GameErrorCode.CONFIRM_NOT_ALLOWED.getMessage(),notCreateUserException.getErrorCode().getMessage());






    }

    @BeforeEach
    void setGameService(){
        gameService = new GameService(mongoTemplate,gameRepository,userServiceClient);
    }



}