package com.a708.drwa.room.service;

import com.a708.drwa.room.data.dto.RoomResponseDto;
import com.a708.drwa.room.domain.Room;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

//    public List<RoomResponseDto> getPopularRooms() {
//        // ZSET에서 스코어가 가장 높은 상위 10개의 방 ID를 조회
//        Set<Integer> debateIds = (int) redisTemplate.opsForZSet().reverseRange("popularRooms", 0, 9);
//
//        List<RoomResponseDto> popularRooms = new ArrayList<>();
//        if (debateIds != null) {
//            for (int roomId : debateIds) {
//                // 각 방 ID에 해당하는 방 정보를 조회합니다.
//                Room room = (Room) redisTemplate.opsForValue().get("room:" + roomId);
//                if (room != null) {
//                    // 조회된 방 정보를 RoomResponseDto 객체로 변환하여 리스트에 추가합니다.
//                    RoomResponseDto dto = new RoomResponseDto(
//                            room.getDebateId(),
//                            room.getTitle(),
//                            room.getHost(),
//                            room.getKeywordA(),
//                            room.getKeywordB(),
//                            room.getTotalNum()
//                    );
//                    popularRooms.add(dto);
//                }
//            }
//        }
//        return popularRooms;
//    }
}
