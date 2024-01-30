package com.a708.drwa.elasticsearch.repository;

import java.util.Optional;
import com.a708.drwa.elasticsearch.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RoomRepository extends ElasticsearchRepository<Room, String>{
    Optional<Room> findByTitle(String title);
    Optional<Room> findByKeyword(String keyword);
}