package com.showtime.gameservice.service;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.dto.GameSearchDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.entity.UserInfo;
import com.showtime.gameservice.repository.GameRepository;
import com.showtime.gameservice.type.GameErrorCode;
import com.showtime.gameservice.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


}
