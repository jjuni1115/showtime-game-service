package com.showtime.gameservice.repository;

import com.showtime.gameservice.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameRepository {


    private final MongoTemplate mongoTemplate;

    public Page<Game> findMainGameList(String keyword, PageRequest pageRequest) {



        Query query = new Query();
        query.addCriteria(Criteria.where("deadlineYn").is(false).and("gameDate").gt(LocalDateTime.now()));
        if(keyword!=null && !keyword.isEmpty()){
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("gameName").regex(keyword, "i"),
                    Criteria.where("address").regex(keyword, "i"),
                    Criteria.where("stadium").regex(keyword, "i")
            ));
        }
        query.with(pageRequest);

        List<Game> gameList = mongoTemplate.find(query, Game.class);

        Page<Game> res = PageableExecutionUtils.getPage(
                gameList,
                pageRequest,
                () -> mongoTemplate.count(query.limit(-1).skip(-1), Game.class)
        );

        return res;
    }

    public Game findGame(String gameId){

        return mongoTemplate.findById(gameId, Game.class);

    }

    public void entryPlayer(Game game){
        mongoTemplate.save(game);
    }

}
