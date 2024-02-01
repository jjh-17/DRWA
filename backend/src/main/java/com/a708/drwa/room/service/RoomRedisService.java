package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import io.lettuce.core.RedisCommandTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomRedisService { // Redis와 상호작용하는 서비스

    @Autowired
    private RedisTemplate<String, Room> redisTemplate;


    public Room getRoomFromRedisByTitle(String title) { // 방 제목으로 방 정보 조회 메서드
        try {
            return redisTemplate.opsForValue().get(title);
        } catch (RedisConnectionFailureException e) {
            // Redis 연결 실패 처리
            System.err.println("Redis 연결 실패: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        } catch (RedisCommandTimeoutException e) {
            // Redis 명령어 실행 시간 초과 처리
            System.err.println("Redis 명령어 실행 시간 초과: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("Redis 조회 중 알 수 없는 오류 발생: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        }
        return null; // 또는 적절한 오류 처리/대응 로직
    }

    public Room getRoomFromRedisByKeyword(String keyword) { // 방 키워드로 레디스에서 방 정보 조회 메서드
        try {
            return redisTemplate.opsForValue().get(keyword);
        } catch (RedisConnectionFailureException e) {
            // Redis 연결 실패 처리
            System.err.println("Redis 연결 실패: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        } catch (RedisCommandTimeoutException e) {
            // Redis 명령어 실행 시간 초과 처리
            System.err.println("Redis 명령어 실행 시간 초과: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        } catch (Exception e) {
            // 기타 예외 처리
            System.err.println("Redis 조회 중 알 수 없는 오류 발생: " + e.getMessage());
            // 로깅 라이브러리 사용 권장
        }
        return null; // 또는 적절한 오류 처리/대응 로직
    }

    public List<Room> getRoomsFromRedis(List<String> roomIds) {
        return redisTemplate.opsForValue().multiGet(roomIds);
    }
}