package com.showtime.gameservice.service;

import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.repository.GameRepository;
import com.showtime.gameservice.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final MongoTemplate mongoTemplate;

    private final GameRepository gameRepository;

    public void saveNewGame(GameDto req){

        //TODO: insert 로직 추가하기

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
    public List<Game> getGameMainList(){
        return gameRepository.findMainGameList();
    }



}
