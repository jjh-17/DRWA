package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GameInfoRepository extends JpaRepository<GameInfo, Integer> {
    // MVP 선정 멤버 ID 수정
    @Query("UPDATE GameInfo g SET g.mvpMemberId=:memberId WHERE g.gameId=:gameId")
    int updateMvp(int gameId, int memberId);
}
