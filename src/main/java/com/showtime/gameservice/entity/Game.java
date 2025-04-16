package com.showtime.gameservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "game_list")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private String id;

    private String game_name;
    private int maxPlayer;
    private int minPlayer;
    private String address;
    private LocalDateTime gameDate;
    private Boolean deadlineYn;
    private String content;
    private String stadium;
    private String gameType;


}
