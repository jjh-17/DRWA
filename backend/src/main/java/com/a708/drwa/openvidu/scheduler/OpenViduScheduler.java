package com.a708.drwa.openvidu.scheduler;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenViduScheduler {
    private final OpenVidu openVidu;

    @Async
    @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.SECONDS)
    public void fetch() {
        try {
            openVidu.fetch();
        } catch (OpenViduException e) {
            log.error("OpenVidu fetch failed");
        }
    }
}
