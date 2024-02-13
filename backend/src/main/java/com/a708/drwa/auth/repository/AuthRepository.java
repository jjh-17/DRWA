package com.a708.drwa.auth.repository;

import com.a708.drwa.auth.domain.RefreshToken;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories
public interface AuthRepository extends CrudRepository<RefreshToken, String> {
}
