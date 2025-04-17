package com.showtime.gameservice.endpoint;

import com.showtime.coreapi.response.ApiResponse;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<Game>>> getGameMainList(){
        Page<Game> gameList = gameService.getGameMainList();

        return ResponseEntity.ok(ApiResponse.ok(gameList, request.getRequestURI()));
    }


}
