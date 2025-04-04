package com.showtime.gameservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDto {

    private String gameName;
    private String gameType;
    private int maxPlayer;
    private int minPlayer;
    private String address;
    private String stadium;
    private String gameDate;

}
