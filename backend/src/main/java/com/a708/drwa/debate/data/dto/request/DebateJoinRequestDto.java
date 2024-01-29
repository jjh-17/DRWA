package com.a708.drwa.debate.data.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DebateJoinRequestDto {
    private int debateId;

    private int userId;

    private List<Integer> viewerList;

    private List<Integer> debateUserList;

}
