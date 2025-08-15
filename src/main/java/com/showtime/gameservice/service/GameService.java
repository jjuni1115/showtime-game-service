package com.showtime.gameservice.service;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.dto.MyGameDto;
import com.showtime.gameservice.dto.GameSearchDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.entity.UserInfo;
import com.showtime.gameservice.kafka.GameEvnetProducer;
import com.showtime.gameservice.repository.GameRepository;
import com.showtime.gameservice.type.GameErrorCode;
import com.showtime.gameservice.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final MongoTemplate mongoTemplate;

    private final GameRepository gameRepository;

    private final UserServiceClient userServiceClient;

    private final GameEvnetProducer gameEvnetProducer;

    @Transactional
    public void saveNewGame(GameDto req) {

        UserInfo createUserInfo = userServiceClient.getLoingUserInfo().getData();


        Game game = Game.builder()
                .gameName(req.getGameName())
                .content(req.getContent())
                .gameType(req.getGameType())
                .gameDate(DateUtil.convertStringToLocalDateTime(req.getGameDate(), "yyyyMMdd HH:mm"))
                .address(req.getAddress())
                .stadium(req.getStadium())
                .deadlineYn(false)
                .closeYn(false)
                .maxPlayer(req.getMaxPlayer())
                .minPlayer(req.getMinPlayer())
                .players(new ArrayList<>())
                .waitingPlayers(new ArrayList<>())
                .createUser(createUserInfo)
                .build();

        mongoTemplate.insert(game);
    }

    @Transactional
    public Page<Game> getGameMainList(GameSearchDto params) {
        return gameRepository.findMainGameList(params.getKeyword(), PageRequest.of(params.getCurrPage(), params.getPageSize()));
    }

    @Transactional
    public Game getGameInfo(String gameId) {

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        return game;
    }

    @Transactional
    public void entryGame(String gameId) {


       UserInfo userInfo = userServiceClient.getLoingUserInfo().getData();

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if (game.getPlayers() != null && (game.getPlayers().contains(userInfo) || game.getWaitingPlayers().contains(userInfo))) {
            throw new CustomRuntimeException(GameErrorCode.USER_ALREADY_REGISTER_EXCEPTION);
        }

        if (game.getPlayers() != null && (game.getMaxPlayer() <= game.getPlayers().size())) {
            throw new CustomRuntimeException(GameErrorCode.CAPACITY_EXCEED_EXCEPTION);
        }

        if (game.getPlayers() == null) {
            game.setPlayers(new ArrayList<>(Arrays.asList(userInfo)));
        }

        game.getWaitingPlayers().add(userInfo);
        gameRepository.entryPlayer(game);


    }

    @Transactional
    public Game entryConfirm(String gameId, Long userId) {

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if (game.getCloseYn()) {

            throw new CustomRuntimeException(GameErrorCode.GAME_ALREADY_CLOSE);

        }

        UserInfo userInfo = userServiceClient.getLoingUserInfo().getData();

        if (!game.getCreateUser().getUserEmail().equals(userInfo.getUserEmail())) {

            throw new CustomRuntimeException(GameErrorCode.CONFIRM_NOT_ALLOWED);

        }

        if (!game.getPlayers().isEmpty() || game.getPlayers().contains(userInfo) ) {
            throw new CustomRuntimeException(GameErrorCode.USER_ALREADY_REGISTER_EXCEPTION);
        }


        UserInfo targetUSer = userServiceClient.getUserInfo(userId).getData();

        game.getPlayers().add(targetUSer);
        game.getWaitingPlayers().removeIf(player->player.getUserId().equals(userId));

        Game gameEntity = gameRepository.playerConfirm(game);

        gameEvnetProducer.sendGameEvent();



        return gameEntity;

    }

    @Transactional
    public Game deletePlayer(String gameId, Long userId) {

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if (game.getCloseYn()) {

            throw new CustomRuntimeException(GameErrorCode.GAME_ALREADY_CLOSE);

        }

        UserInfo userInfo = userServiceClient.getLoingUserInfo().getData();

        if (!game.getCreateUser().getUserEmail().equals(userInfo.getUserEmail())) {

            throw new CustomRuntimeException(GameErrorCode.DELETE_NOT_ALLOWED);

        }

        UserInfo targetUser = userServiceClient.getUserInfo(userId).getData();

        if (!game.getPlayers().contains(targetUser)) {
            throw new CustomRuntimeException(GameErrorCode.PLAYERS_NOT_FOUND);
        }

        game.getPlayers().removeIf(player->player.getUserId().equals(userId));

        Game gameEntity = gameRepository.deletePlayer(game);

        return gameEntity;

    }

    @Transactional
    public Game closeGame(String gameId) {

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if (game.getCloseYn()) {

            throw new CustomRuntimeException(GameErrorCode.GAME_ALREADY_CLOSE);

        }

        UserInfo userInfo = userServiceClient.getLoingUserInfo().getData();

        if (!game.getCreateUser().getUserEmail().equals(userInfo.getUserEmail())) {

            throw new CustomRuntimeException(GameErrorCode.CLOSE_NOT_ALLOWED);

        }

        game.setCloseYn(true);

        Game gameEntity = gameRepository.closeGame(game);

        return gameEntity;


    }
    @Transactional
    public Page<MyGameDto> getMyGameList(GameSearchDto params) {

        UserInfo userInfo = userServiceClient.getLoingUserInfo().getData();

        Page<Game> myGameList = gameRepository.findMyGameList(userInfo.getUserId(), params.getKeyword(), PageRequest.of(params.getCurrPage(), params.getPageSize()));

        List<MyGameDto> filteredGames = myGameList.getContent().stream()
                .map(game -> {
                    String userStatus;
                    if (game.getCreateUser().getUserId().equals(userInfo.getUserId())) {
                        userStatus = "created";
                    } else if (game.getPlayers().stream().anyMatch(player -> player.getUserId().equals(userInfo.getUserId()))) {
                        userStatus = "participating";
                    } else {
                        userStatus = "waiting";
                    }

                    return MyGameDto.builder()
                            .id(game.getId())
                            .gameName(game.getGameName())
                            .maxPlayer(game.getMaxPlayer())
                            .minPlayer(game.getMinPlayer())
                            .address(game.getAddress())
                            .gameDate(game.getGameDate())
                            .deadlineYn(game.getDeadlineYn())
                            .content(game.getContent())
                            .stadium(game.getStadium())
                            .gameType(game.getGameType())
                            .createUser(game.getCreateUser())
                            .closeYn(game.getCloseYn())
                            .players(game.getPlayers())
                            .waitingPlayers(game.getWaitingPlayers())
                            .userStatus(userStatus)
                            .build();
                })
                .filter(myGameDto -> {
                    if (params.getMyGameState() == null || params.getMyGameState().isEmpty()) {
                        return true;
                    }

                    LocalDateTime now = LocalDateTime.now();

                    switch (params.getMyGameState()) {
                        case "1": // Not Started
                            return myGameDto.getGameDate().isAfter(now);
                        case "2": // Finished
                            return myGameDto.getGameDate().isBefore(now);
                        case "3": // Created by me
                            return myGameDto.getUserStatus().equals("created");
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(filteredGames, PageRequest.of(params.getCurrPage(), params.getPageSize()), myGameList.getTotalElements());
    }


}
