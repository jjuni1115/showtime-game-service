package com.showtime.gameservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameEvnetProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    public void sendGameEvent(){
        kafkaTemplate.send("game-event","user permission");
    }

}
