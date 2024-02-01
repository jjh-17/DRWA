package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.repository.RoomSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomSearchService {

    @Autowired
    private RoomSearchRepository roomSearchRepository;

    @Autowired
    private RoomRedisService roomRedisService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Room> searchRooms(String query) {
        return searchRoomsByNori(query); // 기본 검색도 Nori를 사용하도록 설정
    }

    public List<Room> searchRoomsByNori(String query) {
        SearchRequest searchRequest = new SearchRequest("rooms"); // "rooms"는 인덱스 이름
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title", query).analyzer("nori"));
        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            return Arrays.stream(searchResponse.getHits().getHits())
                    .map(hit -> convertHitToRoom(hit))
                    .map(room -> roomRedisService.getRoomFromRedis(room.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Room convertHitToRoom(SearchHit hit) {
        try {
            return objectMapper.readValue(hit.getSourceAsString(), Room.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
