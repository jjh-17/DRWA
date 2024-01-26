package com.a708.drwa.room.controller;

import com.a708.drwa.room.domain.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomSearchService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public List<Room> searchRoomsByNori(String query) {
        Query searchQuery = QueryBuilders.matchQuery("title", query).analyzer("nori").build();
        SearchRequest request = new SearchRequest.Builder()
                .index("rooms")
                .query(searchQuery)
                .build();

        try {
            SearchResponse<Room> response = elasticsearchClient.search(request, Room.class);
            return response.hits().hits().stream()
                    .map(hit -> hit.source())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}


