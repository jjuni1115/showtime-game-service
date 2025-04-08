package com.showtime.gameservice.endpoint;

import com.showtime.coreapi.response.ApiResponse;
import com.showtime.gameservice.dto.GameDto;
import com.showtime.gameservice.entity.Game;
import com.showtime.gameservice.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameEndpoint {

    private final HttpServletRequest request;


    private final GameService gameService;

    @PostMapping("")
    public ResponseEntity<ApiResponse<String>> createGame(@RequestBody GameDto req) {


        gameService.saveNewGame(req);

        return ResponseEntity.ok(ApiResponse.ok("게임 생성 성공", request.getRequestURI()));
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Game>>> getGameMainList(){
        List<Game> gameList = gameService.getGameMainList();

        return ResponseEntity.ok(ApiResponse.ok(gameList, request.getRequestURI()));
    }


}
