package com.showtime.gameservice.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSearchDto {

    @Parameter(
            name = "pageSize"
            ,description = "페이지 사이즈"
            ,example = "10"
            ,in = ParameterIn.QUERY
            ,required = true
    )
    private int pageSize;

    @Parameter(
            name = "currPage"
            ,description = "현재 페이지"
            ,example = "0"
            ,in = ParameterIn.QUERY
            ,required = true
    )
    private int currPage;

    @Parameter(
            name = "keyword"
            ,description = "검색어"
            ,example = "초보"
            ,in = ParameterIn.QUERY
            ,required = false
    )
    private String keyword;

    @Parameter(
            name = "gameType"
            ,description = "경기타입"
            ,example = "1"
            ,in = ParameterIn.QUERY
            ,required = false
    )
    private String gameType;

    @Parameter(
            name = "myGameState"
            ,description = "나의게임키워드"
            ,example = "1"
            ,in = ParameterIn.QUERY
            ,required = false
    )
    private String myGameState;

}
