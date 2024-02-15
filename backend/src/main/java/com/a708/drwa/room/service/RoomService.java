package com.a708.drwa.room.service;

import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.dto.response.DebateInfoListResponse;
import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.util.DebateUtil;
import com.a708.drwa.room.data.Condition;
import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RoomRepository roomRepository;
    private final DebateUtil debateUtil;

    public DebateInfoListResponse findAll() {
        List<DebateInfoResponse> list = StreamSupport
                .stream(roomRepository.findAll().spliterator(), false)
                .map(Room::toDto)
                .toList();
        for (DebateInfoResponse debateInfoResponse : list) {
            debateInfoResponse.setLists(debateUtil.getList(debateInfoResponse.getSessionId()));
        }

        return DebateInfoListResponse.builder()
                        .debateInfoResponses(list)
                .build();
    }

    public DebateInfoListResponse findByCondition(String condition, String value, Pageable pageable) {
        Page<Room> page = null;
        if(condition.equals(Condition.TITLE))
            page = roomRepository.findByTitle(pageable, value);
        else if(condition.equals(Condition.KEYWORD))
            page = roomRepository.findByLeftKeywordOrRightKeyword(pageable, value, value);
        else
            throw new IllegalArgumentException();

        return DebateInfoListResponse.builder()
                .debateInfoResponses(page.stream()
                        .map(Room::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

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
