package com.yy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class YuanyaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(YuanyaoApplication.class ,args);
    }
}
