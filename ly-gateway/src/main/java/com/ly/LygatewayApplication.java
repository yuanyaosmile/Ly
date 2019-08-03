package com.ly;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringCloudApplication
@EnableZuulProxy
public class LygatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(LygatewayApplication.class, args);
    }
}
