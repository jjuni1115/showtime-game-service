package com.showtime.gameservice.config;

import com.showtime.gameservice.interceptor.AuthRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public AuthRequestInterceptor feignRequestInterceptor() {
        return new AuthRequestInterceptor();
    }


}
