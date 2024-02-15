package com.a708.drwa.room.repository;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories
public interface RoomRepository extends ElasticsearchRepository<Room, String> {
    Page<Room> findByTitle(Pageable pageable, String title);
    Page<Room> findByLeftKeywordOrRightKeyword(Pageable pageable, String keyword1, String keyword2);
}
