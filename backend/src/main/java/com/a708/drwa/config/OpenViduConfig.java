package com.a708.drwa.config;

import io.openvidu.java.client.OpenVidu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenViduConfig {
    @Value("${openvidu.hostname}")
    private String hostName;

    @Value("${openvidu.secret}")
    private String secret;

    @Bean
    public OpenVidu openVidu() {
        return new OpenVidu(hostName, secret);
    }
}
