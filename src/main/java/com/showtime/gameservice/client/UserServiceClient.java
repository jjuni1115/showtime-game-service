package com.showtime.gameservice.client;

import com.showtime.coreapi.feign.FeignConfig;
import com.showtime.coreapi.feign.ResponseDto;
import com.showtime.gameservice.entity.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "SHOWTIME-USER-SERVICE", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/user/login-user-info")
    ResponseDto<UserInfo> getLoingUserInfo();

    @GetMapping("/user/user-info/{userId}")
    ResponseDto<UserInfo> getUserInfo(@PathVariable("userId") Long userId);

}
