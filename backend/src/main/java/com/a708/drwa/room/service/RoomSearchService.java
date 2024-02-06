package com.a708.drwa.room.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.a708.drwa.room.domain.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomSearchService {
    private ElasticsearchClient elasticsearchClient;
    private RedisTemplate<String, Room> redisTemplate;

    public List<Room> searchRooms(String field, String query) {
        SearchRequest request = new SearchRequest.Builder()
                .index("room_index")
                .query(q -> q.match(m -> m.field(field).query(query)))
                .build();

        try {
            SearchResponse<Room> response = elasticsearchClient.search(request, Room.class);
            return response.hits().hits().stream()
                    .map(hit -> {
                        Room room = hit.source();
                        room.setTotalNum(hit.source().getTotalNum());
                        room.setHost(hit.source().getHost());
                        room.setTitle(hit.source().getTitle());
                        room.setKeyword(hit.source().getKeyword());
                        return room;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    private void saveRoomInRedis(Room room) {
        if (room != null && room.getDebateId() != null) {
            redisTemplate.opsForValue().set(room.getDebateId(), room);
        }
    }
    private Room fetchRoomFromRedis(String roomId) {
        Object result = redisTemplate.opsForValue().get(roomId);
        if (result instanceof Room) {
            return (Room) result;
        }
        return null;
    }
}