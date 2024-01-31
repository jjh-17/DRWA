package com.a708.drwa.room.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.a708.drwa.room.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomSearchService { // redis에서 방 검색하는 서비스
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private RedisTemplate<String, Room> redisTemplate;

    public List<Room> searchRoomsByNori(String query) {
        // nori 분석기를 통해 elasticsearch에서 검색어와 일치하는 방 정보 반환
        SearchRequest request = new SearchRequest.Builder()
                .index("room_index")
                .query(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field("title")
                                                .query(query)
                                                .analyzer("nori")
                                        )
                                )
                                .should(s -> s
                                        .match(m -> m
                                                .field("keyword")
                                                .query(query)
                                                .analyzer("nori")
                                        )
                                )
                        )
                )
                .build();

        try {
            SearchResponse<Room> response = elasticsearchClient.search(request, Room.class);
            return response.hits().hits().stream()
                    .map(hit -> redisTemplate.opsForValue().get(hit.id())) // Redis에서 ID를 키로 사용하여 데이터 조회
                    .filter(room -> room != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}


