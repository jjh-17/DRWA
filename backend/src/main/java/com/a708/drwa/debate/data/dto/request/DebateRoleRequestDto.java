package com.a708.drwa.debate.data.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DebateRoleRequestDto {
    private int debateId;

    private int userId;

    // 0 : teamA,   1: teamB,   2 : 배심원,    3: 관전자
    private int clickRole;

    private List<Integer> teamAList;
    private List<Integer> teamBList;
    private List<Integer> jurorList;
    private List<Integer> viewerList;
}
