package com.a708.drwa.room.repository;

import java.util.Optional;
import com.a708.drwa.room.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RoomRepository extends ElasticsearchRepository<Room, String>{
    Optional<Room> findByTitle(String title);
    Optional<Room> findByKeyword(String keyword);
}
