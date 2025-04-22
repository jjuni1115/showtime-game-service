package com.showtime.gameservice.interceptor;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
public class AuthRequestInterceptor implements RequestInterceptor {



    @Override
    public void apply(RequestTemplate requestTemplate) {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        requestTemplate.header("Authorization",request.getHeader("Authorization"));
    }
}
