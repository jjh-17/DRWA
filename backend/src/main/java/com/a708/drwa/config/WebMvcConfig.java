package com.a708.drwa.config;

import io.openvidu.java.client.OpenVidu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowCredentials(false);
    }

    @Bean
    public OpenVidu openVidu(@Value("${OPENVIDU_HOSTNAME}") String OPENVIDU_URL,
                         @Value("${OPENVIDU_SECRET}") String OPENVIDU_SECRET) {
        return new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }
}
