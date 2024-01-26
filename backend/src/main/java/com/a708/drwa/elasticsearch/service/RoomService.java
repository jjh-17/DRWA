package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.repository.RoomRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomRedisService {

    @Autowired
    private RedisTemplate<String, Room> redisTemplate;


    public Room getRoomFromRedisByTitle(String title) {

        return redisTemplate.opsForValue().get(title);
    }

    public Room getRoomFromRedisByKeyword(String keyword) {

        return redisTemplate.opsForValue().get(keyword);
    }

    public List<Room> getRoomsFromRedis(List<String> roomIds) {
        return redisTemplate.opsForValue().multiGet(roomIds);
    }
}