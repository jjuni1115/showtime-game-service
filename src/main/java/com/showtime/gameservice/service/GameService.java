package com.showtime.gameservice.service;

import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.entity.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {

    private final MongoTemplate mongoTemplate;

    public void saveNewGame(){

        //TODO: insert 로직 추가하기

        Game game = Game.builder()
                .game_name("테스트 게임")
                .content("4월4일 22시에 올공에서 농구하실 분 구합니다. 6명이서 3대3 간단하게 1시간만용 공 있으니 몸만 오시면 됩니다.")
                .gameType("1")
                .gameDate("2024-04-04 22:00")
                .address("서울특별시 송파구 올림픽공원 농구장")
                .stadium("올림픽공원 농구장")
                .deadlineYn(false)
                .maxPlayer(6)
                .minPlayer(6)
                .build();

        mongoTemplate.insert(game);
    }



}
