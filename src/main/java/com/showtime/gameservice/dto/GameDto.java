package com.showtime.gameservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameDto {

    @NotNull
    @Schema(description = "농구 게임 방 제목", type = "String", nullable = false, example = "3:3 농구 하실 분 구합니다.")
    private String gameName;

    @NotNull
    @Schema(description = "게임 종류", type = "String", nullable = false, example = "1 - 연습게임, 2- 정규게임")
    private String gameType;

    @NotNull
    @Schema(description = "최대 모집 인원", type = "int", nullable = false, example = "10")
    private int maxPlayer;

    @NotNull
    @Schema(description = "최소 모집 인원", type = "int", nullable = false, example = "2")
    private int minPlayer;

    @NotNull
    @Schema(description = "게임 주소", type = "String", nullable = false, example = "서울시 강남구 일원동")
    private String address;

    @NotNull
    @Schema(description = "경기장명", type = "String", nullable = false, example = "마루공원 3번 코트")
    private String stadium;

    @NotNull
    @Schema(description = "게임 시작 날짜", type = "String", nullable = false, example = "20250510 18:30 yyyyMMdd HH:mm")
    private String gameDate;

    @NotNull
    @Schema(description = "게임 내용", type = "String", nullable = false, example = "가볍게 슛 던지실 분 구합니다.")
    private String content;

}
