package com.showtime.gameservice.repository;

import com.showtime.gameservice.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameRepository {


    private final MongoTemplate mongoTemplate;

    public List<Game> findMainGameList(){
        Query query = new Query();
        query.addCriteria(Criteria.where("deadlineYn").is(false));

        List<Game> gameList = mongoTemplate.find(query, Game.class);

        return gameList;
    }

}
