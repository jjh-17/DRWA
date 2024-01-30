package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameInfoRepository extends JpaRepository<GameInfo, Integer> {
}
