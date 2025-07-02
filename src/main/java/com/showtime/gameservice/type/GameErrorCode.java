package com.showtime.gameservice.type;

import com.showtime.coreapi.type.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {


    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"500001","서버 내부 오류가 발생했습니다."),
    CAPACITY_EXCEED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "520001","게임 정원이 초과되었습니다."),
    USER_ALREADY_REGISTER_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "520002","이미 참가 신청 된 게임입니다."),
    GAME_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "520003","존재하지 않는 게임입니다."),
    GAME_ALREADY_CLOSE(HttpStatus.INTERNAL_SERVER_ERROR,"520004","이미 모집 종료된 게임입니다."),
    CLOSE_NOT_ALLOWED(HttpStatus.INTERNAL_SERVER_ERROR,"520005","모집종료 권한이 없습니다."),
    CONFIRM_NOT_ALLOWED(HttpStatus.INTERNAL_SERVER_ERROR,"520006","참가 확정 권한이 없습니다."),;


    private final HttpStatus status;
    private final String code;
    private final String message;


    private static final Map<String, GameErrorCode> CODE_MAP = Arrays.stream(values()).collect(Collectors.toMap(GameErrorCode::getCode, e -> e));

    public static GameErrorCode fromCode(String code){
        return CODE_MAP.get(code);
    }


}
