package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RoomService(RedisTemplate<String, Object> redisTemplate) { this.redisTemplate = redisTemplate; }

    public Room findRoomById(String sessionId) {
        return (Room) redisTemplate.opsForValue().get("room:" + sessionId);
    }

    public void saveRoomInRedis(Room room) {
        if (room != null && room.getSessionId() != null) {
            redisTemplate.opsForValue().set("room:" + room.getSessionId(), room);
        }
    }

    public void updateRoomThumbnail(Room.ThumbnailUpdateInfo thumbnailUpdateInfo) {
        Room room = findRoomById(thumbnailUpdateInfo.getSessionId());
        if (room != null) {
            room.updateThumbnail(thumbnailUpdateInfo);
            saveRoomInRedis(room); // 업데이트된 정보를 레디스에 저장
        }
    }
}
