package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomRedisService { // Redis와 상호작용하는 서비스

    private final RedisTemplate<String, Room> redisTemplate;


    public Room getRoomFromRedisByTitle(String title) { // 방 제목으로 방 정보 조회 메서드
        return redisTemplate.opsForValue().get(title);
    }

    public Room getRoomFromRedisByKeyword(String keyword) { // 방 키워드로 레디스에서 방 정보 조회 메서드
        return redisTemplate.opsForValue().get(keyword);
    }

    public List<Room> getRoomsFromRedis(List<String> roomIds) {

        return redisTemplate.opsForValue().multiGet(roomIds);
    }
}