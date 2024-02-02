package com.a708.drwa.room.repository;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

//public interface RoomSearchRepository extends ElasticsearchRepository<Room, Long> {
//
//    List<Room> findByTitleContaining(String title);
//    List<Room> findByKeywordContaining(String keyword);
//}
// RoomSearchService에서 ElasticsearchClient를 사용하여 검색을 하므로 없어도 될 듯 !