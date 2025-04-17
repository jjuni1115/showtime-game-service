package com.showtime.gameservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(new Server().url("http://172.16.111.164:8000/game-service")))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Showtime Game Service API")
                        .version("v0.0.1")
                        .description("농구 게임 관련 메인 API"));
    }


}
