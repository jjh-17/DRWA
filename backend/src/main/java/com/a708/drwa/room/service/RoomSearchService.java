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
        SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder().index("room_index");
        if ("title".equals(field)) {
            // 제목으로 검색
            searchRequestBuilder.query(q -> q.match(m -> m.field("title").query(query)));
        } else if ("keyword".equals(field)) {
            // keywordA 또는 keywordB로 검색
            searchRequestBuilder.query(q -> q.bool(b -> b
                    .should(s -> s.match(m -> m.field("keywordA").query(query)))
                    .should(s -> s.match(m -> m.field("keywordB").query(query)))
            ));
        } else {
            // 지원하지 않는 검색 필드의 경우 빈 결과 반환
            return Collections.emptyList();
        }
        SearchRequest request = searchRequestBuilder.build();
        try {
            SearchResponse<Room> response = elasticsearchClient.search(request, Room.class);
            return response.hits().hits().stream()
                    .map(hit -> {
                        Room room = hit.source();
                        room.setTotalNum(hit.source().getTotalNum());
                        room.setHost(hit.source().getHost());
                        room.setTitle(hit.source().getTitle());
                        room.setKeywordA(hit.source().getKeywordA());
                        room.setKeywordB(hit.source().getKeywordB());
                        return room;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}