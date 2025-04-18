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

}
