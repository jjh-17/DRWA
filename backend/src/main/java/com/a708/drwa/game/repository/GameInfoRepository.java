package com.a708.drwa.game.repository;

import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestRedisDto;
import com.a708.drwa.game.data.dto.response.GameInfoCreateResponseRedisDto;
import com.a708.drwa.game.domain.GameInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameInfoRepository extends JpaRepository<GameInfo, Integer> {

    // MVP 선정 멤버 ID 수정
    @Query("UPDATE GameInfo g SET g.mvpMemberId=:memberId WHERE g.gameId=:gameId")
    int updateMvp(int gameId, int memberId);

    // Redis에서 게임 정보 생성을 위해 필요한 데이터를 받아옴
    default GameInfoCreateResponseRedisDto getGameInfoCreateResponseRedisDto(GameInfoCreateRequestRedisDto gameInfoCreateRequestRedisDto) {
        // TODO : Redis에서 데이터 가져올 것!
        String keyword = "keyword";

        return GameInfoCreateResponseRedisDto.builder()
                .keyword(keyword)
                .build();
    }

}
