package com.a708.drwa.room.service;


import com.a708.drwa.room.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomSearchService {
    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private RoomRedisService roomRedisService;

    public List<Room> searchRoomsByNori(String query) {
        Query searchQuery = Query.of(q -> q
                .match(m -> m
                        .field("title")
                        .query(query)
                        .analyzer("nori")
                )
        );
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("rooms")
                .query(searchQuery)
                .build();


        try {
            SearchResponse<Room> searchResponse = elasticsearchClient.search(searchRequest, Room.class);
            // Elasticsearch 검색 결과 처리
            return searchResponse.hits().hits().stream()
                    .map(hit -> roomRedisService.getRoomFromRedis(hit.id()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (ElasticsearchException e) {
            return Collections.emptyList();
        }
    }



}
