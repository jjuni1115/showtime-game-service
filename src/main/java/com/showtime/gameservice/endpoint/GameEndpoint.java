package com.showtime.gameservice.endpoint;

import com.showtime.gameservice.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameEndpoint {


    private final GameService gameService;

    @PostMapping("")
    public String createGame() {


        gameService.saveNewGame();

        return "Game created";
    }


}
