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

    public List<Room> searchRoomsByTitle(String query) {
        return searchRooms("title", query);
    }

    public List<Room> searchRoomsByKeyword(String query) {
        return searchRooms("keyword", query);
    }

    private List<Room> searchRooms(String field, String query) {
        SearchRequest request = new SearchRequest.Builder()
                .index("room_index")
                .query(q -> q
                        .match(m -> m
                                .field(field)
                                .query(query)
                                .analyzer("nori")
                        )
                )
                .build();

        try {
            SearchResponse<Room> response = elasticsearchClient.search(request, Room.class);
            List<Room> rooms = response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());

            rooms.forEach(this::saveRoomInRedis);

            return rooms;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    private void saveRoomInRedis(Room room) {
        if (room != null && room.getId() != null) {
            redisTemplate.opsForValue().set(room.getId(), room);
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