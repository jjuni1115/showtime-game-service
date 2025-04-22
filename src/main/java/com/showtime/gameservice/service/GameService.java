package com.showtime.gameservice.service;

import com.showtime.gameservice.client.UserServiceClient;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.dto.GameSearchDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.repository.GameRepository;
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
                .game_name(req.getGameName())
                .content(req.getContent())
                .gameType(req.getGameType())
                .gameDate(DateUtil.convertStringToLocalDateTime(req.getGameDate(),"yyyyMMdd HH:mm"))
                .address(req.getAddress())
                .stadium(req.getStadium())
                .deadlineYn(false)
                .maxPlayer(req.getMaxPlayer())
                .minPlayer(req.getMinPlayer())
                .build();

        mongoTemplate.insert(game);
    }

    @Transactional
    public Page<Game> getGameMainList(GameSearchDto params) {
        return gameRepository.findMainGameList(params.getKeyword(), PageRequest.of(params.getCurrPage(), params.getPageSize()));
    }

    @Transactional
    public void entryGame(String gameId){


        String userEmail = userServiceClient.getUserId();

        log.info("email : {}", userEmail);



    }




}
