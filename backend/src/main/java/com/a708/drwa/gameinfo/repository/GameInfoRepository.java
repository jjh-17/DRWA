package com.a708.drwa.gameinfo.repository;

import com.a708.drwa.gameinfo.domain.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameInfoRepository extends JpaRepository<GameInfo, Integer> {
}
