package com.a708.drwa.game.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AddGameResponseDto {
    private int teamAVoteNum;               // 팀A 투표 수
    private int teamBVoteNum;               // 팀B 투표 수
    private int noVoteNum;                  // 미투표 수

    private List<Integer> mvpList;          // MVP로 선정된 멤버 ID 리스트
    private int mvpPoint;                   // MVP 추가 포인트

    private int winnerPoint;                // 승리팀 획득 포인트
    private String winnerTeam;              // 승리 팀
    private List<Integer> winnerTeamList;   // 승리 팀 리스트
    
    private final boolean isPrivate;        // 사설방 여부
}
