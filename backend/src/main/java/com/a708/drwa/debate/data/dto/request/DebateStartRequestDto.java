package com.a708.drwa.debate.data.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DebateStartRequestDto {
    private int debateId;

    private List<Integer> teamAList;
    private List<Integer> teamBList;
    private List<Integer> jurorList;
    private List<Integer> viewerList;
}
