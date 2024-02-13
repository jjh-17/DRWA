package com.a708.drwa.openvidu.repository;

import com.a708.drwa.openvidu.domain.DebateRoomInfo;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableRedisRepositories
public interface DebateRoomRepository extends CrudRepository<DebateRoomInfo, String> {
    Optional<DebateRoomInfo> findByTitle(String title);
    boolean existsByTitle(String title);
    void deleteByTitle(String title);
}
