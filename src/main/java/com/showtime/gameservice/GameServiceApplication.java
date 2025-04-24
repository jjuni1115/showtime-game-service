package com.showtime.gameservice;

import com.showtime.coreapi.type.ErrorCodeRegistry;
import com.showtime.gameservice.type.GameErrorCode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMongoRepositories
@EnableFeignClients
public class GameServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init(){

        for(GameErrorCode errorCode : GameErrorCode.values()){
            ErrorCodeRegistry.registerErrorCode(errorCode);
        }

    }

}
