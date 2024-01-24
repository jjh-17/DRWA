package com.a708.drwa.game.repository;

import com.a708.drwa.game.data.dto.request.RecordRequestRedisDto;
import com.a708.drwa.game.data.dto.response.RecordResponseRedisDto;
import com.a708.drwa.game.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Integer> {

    // Redis에서 게임 결과 정산을 위해 필요한 데이터를 받아옴
    default RecordResponseRedisDto getRecordResponseRedisDto(RecordRequestRedisDto gameResultRequestRedisDto) {
        // TODO : Redis에서 데이터 가져올 것!
        // TODO : MVP는 어떻게 처리할 것인지 생각해볼 것!
        final List<String> teamAList = List.of("A1", "A2");
        final List<String> teamBList = List.of("B1", "B2");
        final List<String> jurorList = List.of("J1", "J2");
        final List<String> viewerList = List.of("V1", "V2");
        final int voteTeamA = 0;
        final int voteTeamB = 1;
        final String keyword = "keyword";

        return RecordResponseRedisDto.builder()
                .teamAList(teamAList)
                .teamBList(teamBList)
                .jurorList(jurorList)
                .viewerList(viewerList)
                .voteTeamA(voteTeamA)
                .voteTeamB(voteTeamB)
                .keyword(keyword)
                .build();
    }
}
