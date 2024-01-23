package com.a708.drwa.debate.data.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DebateJoinRequestDto {
    private int debateId;
    // TODO: 사용자 정보도 받을 듯?
    // private JWT Token;
}
