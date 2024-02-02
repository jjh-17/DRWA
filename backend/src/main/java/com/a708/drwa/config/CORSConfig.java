package com.a708.drwa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer{
    /**
     * CORS 설정
     * @param registry : CORS 설정을 추가할 레지스트리
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
            .allowedOrigins("http://localhost:5173") // 허용할 주소
            .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS") // 허용할 HTTP method
            .allowedHeaders("*") // 모든 헤더 허용
            .allowCredentials(true); // 쿠키를 주고 받을 수 있도록 허용
    }
}
