package com.showtime.gameservice.service;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.dto.GameSearchDto;
import com.showtime.gameservice.entity.Game;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final MongoTemplate mongoTemplate;

    private final GameRepository gameRepository;

    private final UserServiceClient userServiceClient;

    @Transactional
    public void saveNewGame(GameDto req){


        Game game = Game.builder()
                .gameName(req.getGameName())
                .content(req.getContent())
                .gameType(req.getGameType())
                .gameDate(DateUtil.convertStringToLocalDateTime(req.getGameDate(),"yyyyMMdd HH:mm"))
                .address(req.getAddress())
                .stadium(req.getStadium())
                .deadlineYn(false)
                .maxPlayer(req.getMaxPlayer())
                .minPlayer(req.getMinPlayer())
                .players(new ArrayList<>())
                .build();

        mongoTemplate.insert(game);
    }

    @Transactional
    public Page<Game> getGameMainList(GameSearchDto params) {
        return gameRepository.findMainGameList(params.getKeyword(), PageRequest.of(params.getCurrPage(), params.getPageSize()));
    }

    @Transactional
    public void entryGame(String gameId){


        ResponseDto<String> userIdRes = userServiceClient.getUserId();

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if(game.getPlayers()!=null && game.getPlayers().contains(userIdRes.getData())){
            throw new CustomRuntimeException(GameErrorCode.USER_ALREADY_REGISTER_EXCEPTION);
        }

        if( game.getPlayers() != null && (game.getMaxPlayer() <= game.getPlayers().size())){
            throw new CustomRuntimeException(GameErrorCode.CAPACITY_EXCEED_EXCEPTION);
        }

        if(game.getPlayers()==null){
            game.setPlayers(List.of(userIdRes.getData()));
        }

        game.getPlayers().add(userIdRes.getData());
        gameRepository.entryPlayer(game);




    }

    @Transactional
    public Game closeGame(String gameId){

        Game game = gameRepository.findGame(gameId).orElseThrow(() -> new CustomRuntimeException(GameErrorCode.GAME_NOT_FOUND));

        if(game.getDeadlineYn()){

            throw new CustomRuntimeException(GameErrorCode.GAME_ALREADY_CLOSE);

        }

        ResponseDto<String> userIdRes = userServiceClient.getUserId();

        if(!game.getCreateUserId().equals(userIdRes.getData())){

            throw new CustomRuntimeException(GameErrorCode.CLOSE_NOT_ALLOWED);

        }

        game.setDeadlineYn(true);

        Game gameEntity = gameRepository.closeGame(game);

        return gameEntity;







    }




}
