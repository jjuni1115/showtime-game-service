package com.showtime.gameservice.dto;

import com.showtime.gameservice.entity.UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyGameDto {
    private String id;
    private String gameName;
    private int maxPlayer;
    private int minPlayer;
    private String address;
    private LocalDateTime gameDate;
    private Boolean deadlineYn;
    private String content;
    private String stadium;
    private String gameType;
    private UserInfo createUser;
    private Boolean closeYn;
    private List<UserInfo> players;
    private List<UserInfo> waitingPlayers;
    private String userStatus;
}
