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

    public Page<Game> findMainGameList(){



        Query query = new Query();
        query.addCriteria(Criteria.where("deadlineYn").is(false).and("gameDate").gt(LocalDateTime.now()));
        query.with(PageRequest.of(0,5));

        List<Game> gameList = mongoTemplate.find(query, Game.class);

        Page<Game> res = PageableExecutionUtils.getPage(
                gameList,
                PageRequest.of(0, 5),
                () -> mongoTemplate.count(query, Game.class)
        );

        return res;
    }

}
