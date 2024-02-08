package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RoomService(RedisTemplate<String, Object> redisTemplate) { this.redisTemplate = redisTemplate; }

    public Room findRoomById(String roomId) {
        return (Room) redisTemplate.opsForValue().get("room:" + roomId);
    }

    public void saveRoomInRedis(Room room) {
        if (room != null && room.getDebateId() != null) {
            redisTemplate.opsForValue().set("room:" + room.getDebateId(), room);
        }
    }
}
