package com.showtime.gameservice.repository;

import com.showtime.gameservice.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.Arrays;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GameRepository {


    private final MongoTemplate mongoTemplate;

    public Page<Game> findMainGameList(String keyword, PageRequest pageRequest) {



        Query query = new Query();
        query.addCriteria(Criteria.where("closeYn").is(false).and("gameDate").gt(LocalDateTime.now()));
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

    public Optional<Game> findGame(String gameId){

        return Optional.of(mongoTemplate.findById(gameId, Game.class));

    }

    public void entryPlayer(Game game){
        mongoTemplate.save(game);
    }

    public Game closeGame(Game game){ return mongoTemplate.save(game); }

    public Game playerConfirm(Game game) { return mongoTemplate.save(game); }

    public Game deletePlayer(Game game){return mongoTemplate.save(game);}

    public Page<Game> findMyGameList(Long userId, String keyword, PageRequest pageRequest) {

        // 1. Match Stage: Find games relevant to the user
        Criteria userCriteria = new Criteria().orOperator(
                Criteria.where("createUser.userId").is(userId),
                Criteria.where("players.userId").is(userId),
                Criteria.where("waitingPlayers.userId").is(userId)
        );

        Criteria keywordCriteria = new Criteria();
        if (keyword != null && !keyword.isEmpty()) {
            keywordCriteria.orOperator(
                    Criteria.where("gameName").regex(keyword, "i"),
                    Criteria.where("address").regex(keyword, "i"),
                    Criteria.where("stadium").regex(keyword, "i")
            );
        }

        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(userCriteria, keywordCriteria));

        // 2. AddFields Stage: Create fields for sorting
        AddFieldsOperation addFieldsOperation = Aggregation.addFields()
                .addFieldWithValue("isUpcoming",
                        new Document("$and", Arrays.asList(
                                new Document("$eq", Arrays.asList("$closeYn", false)),
                                new Document("$gt", Arrays.asList("$gameDate", LocalDateTime.now()))
                        ))
                )
                .addFieldWithValue("statusOrder",
                        new Document("$cond", new Document("if", new Document("$eq", Arrays.asList("$createUser.userId", userId)))
                                .append("then", 1)
                                .append("else", new Document("$cond", new Document("if", new Document("$in", Arrays.asList(userId, "$players.userId")))
                                        .append("then", 2)
                                        .append("else", 3)
                                ))
                        )
                ).build();


        // 3. Sort Stage: Sort by the new fields
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "isUpcoming")
                .and(Sort.Direction.ASC, "gameDate")
                .and(Sort.Direction.ASC, "statusOrder")
                .and(Sort.Direction.DESC, "_id");

        // 4. Pagination Stages
        SkipOperation skipOperation = Aggregation.skip((long) pageRequest.getPageNumber() * pageRequest.getPageSize());
        LimitOperation limitOperation = Aggregation.limit(pageRequest.getPageSize());

        // Create aggregation pipeline for fetching the data
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, addFieldsOperation, sortOperation, skipOperation, limitOperation);

        List<Game> gameList = mongoTemplate.aggregate(aggregation, "game_list", Game.class).getMappedResults();

        // Create aggregation pipeline for counting total documents
        Aggregation countAggregation = Aggregation.newAggregation(matchOperation, addFieldsOperation, Aggregation.count().as("total"));
        long total;
        if (!mongoTemplate.aggregate(countAggregation, "game_list", Document.class).getMappedResults().isEmpty()) {
            total = mongoTemplate.aggregate(countAggregation, "game_list", Document.class).getMappedResults().get(0).getInteger("total");
        } else {
            total = 0;
        }


        return PageableExecutionUtils.getPage(gameList, pageRequest, () -> total);
    }

}
