package com.a708.drwa.debate.scheduler;

import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import com.a708.drwa.debate.repository.DebateRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DebateScheduler {
    private final RedisTemplate<String, DebateInfoResponse> redisTemplate;
    private final DebateRoomRepository debateRoomRepository;

    @Async
    @Scheduled(fixedDelay = 45, timeUnit = TimeUnit.SECONDS)
    public void updatePopularDebates() {
        log.info("Update Popular rooms....");
        // 인기 랭킹 초기화
        ZSetOperations<String, DebateInfoResponse> zset = redisTemplate.opsForZSet();
        zset.removeRange(RoomsKey.ROOM_POPULAR_KEY, 0, -1);
        // debateInfo zset에 넣기
        Iterable<DebateRoomInfo> debateRoomInfos = debateRoomRepository.findTop5ByOrderByTotalCntDesc();
        for (DebateRoomInfo debateRoomInfo : debateRoomInfos) {
            log.info("found debateInfo ! : {}", debateRoomInfo.getSessionId());
            zset.add(RoomsKey.ROOM_POPULAR_KEY,
                    debateRoomInfo.toResponse(),
                    debateRoomInfo.getTotalCnt());
        }

        log.info("{} room found ! Update finish !", zset.size(RoomsKey.ROOM_POPULAR_KEY));
    }
}
