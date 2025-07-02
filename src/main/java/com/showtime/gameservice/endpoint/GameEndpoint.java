package com.showtime.gameservice.endpoint;

import com.showtime.coreapi.response.ApiResponse;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.dto.GameSearchDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "game api", description = "농구 게임 관련 API")
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameEndpoint {

    private final HttpServletRequest request;


    private final GameService gameService;

    @Operation(summary = "농구 게임 생성 API", description = "신규 농구 게임 등록 API")
    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> createGame(@RequestBody @Valid GameDto req) {


        gameService.saveNewGame(req);

        return ResponseEntity.ok(ApiResponse.ok("게임 생성 성공", request.getRequestURI()));
    }

    @Operation(summary = "게임 리스트 조회", description = "게임 리스트 조회 API")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<Game>>> getGameMainList(@ParameterObject GameSearchDto params) {
        Page<Game> gameList = gameService.getGameMainList(params);

        return ResponseEntity.ok(ApiResponse.ok(gameList, request.getRequestURI()));
    }

    @Operation(summary = "게임 참가 요청")
    @PutMapping("/entry/{gameId}")
    public ResponseEntity<ApiResponse<String>> entryGame(@PathVariable(value = "gameId") String gameId){

        gameService.entryGame(gameId);

        return ResponseEntity.ok(ApiResponse.ok("게임 참가 성공", request.getRequestURI()));

    }

    @Operation(summary = "게임 참가 확정")
    @PutMapping("/entry/confirm/{gameId}/userId")
    public ResponseEntity<ApiResponse<Game>> entryConfirm(
            @PathVariable(value = "gameId") String gameId, @PathVariable(value = "userId") String userId){


        Game game = gameService.entryConfirm(gameId,userId);

        return ResponseEntity.ok(ApiResponse.ok(game,request.getRequestURI()));


    }

    @Operation(summary = "게임 모집 종료")
    @DeleteMapping("/close-game/{gameId}")
    public ResponseEntity<ApiResponse<Game>> closeGame(
            @PathVariable(value = "gameId") String gameId){

        Game game = gameService.closeGame(gameId);

        return ResponseEntity.ok(ApiResponse.ok(game,request.getRequestURI()));

    }


}
