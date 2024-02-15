package com.a708.drwa.debate.repository;

import com.a708.drwa.debate.domain.DebateRoomInfo;
import com.a708.drwa.debate.enums.DebateCategory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableRedisRepositories
public interface DebateRoomRepository extends CrudRepository<DebateRoomInfo, String> {
    // 카테고리 별 인기순
    List<DebateRoomInfo> findAllByDebateCategoryOrderByTotalCntDesc(DebateCategory debateCategory);
    // 전체 목록
    @Override
    List<DebateRoomInfo> findAll();

    // 인기순
    Iterable<DebateRoomInfo> findTop5ByOrderByTotalCntDesc();
}
