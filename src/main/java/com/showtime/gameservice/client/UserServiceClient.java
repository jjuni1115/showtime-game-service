package com.showtime.gameservice.client;

import com.showtime.coreapi.feign.FeignConfig;
import com.showtime.coreapi.feign.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "SHOWTIME-USER-SERVICE", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/user/user-id")
    ResponseDto<String> getUserId();

}
