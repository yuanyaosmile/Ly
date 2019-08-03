package com.ly.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class GlobalCorsConfig {
    @Bean
    public CorsFilter corsFilter(){
        //添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //允许的域，不要写*，否则cookie就无法使用了
        config.addAllowedOrigin("*");
        //是否发送cookie信息
        config.setAllowCredentials(true);
        //允许请求的方式
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        //允许的头信息
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);

        //添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**", config);

        //返回新的CorsFilter
        return new CorsFilter(configurationSource);
    }
}
