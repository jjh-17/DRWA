package com.a708.drwa.room.repository;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


import java.util.List;


public interface RoomSearchRepository extends ElasticsearchRepository<Room, Long> {

    List<Room> findByTitleContaining(String title);
    List<Room> findByKeywordContaining(String keyword);
}