package com.a708.drwa.debate.repository;

import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import com.a708.drwa.debate.enums.DebateCategory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@EnableRedisRepositories
public interface DebateRoomRepository extends CrudRepository<DebateRoomInfo, String> {
    Optional<DebateRoomInfo> findByTitle(String title);
    boolean existsByTitle(String title);
    void deleteByTitle(String title);

    List<DebateRoomInfo> findAllByDebateCategoryOrderByTotalCntDesc(DebateCategory debateCategory);
    @Override
    List<DebateRoomInfo> findAll();

    Iterable<DebateRoomInfo> findTop5ByOrderByTotalCnt();
}
