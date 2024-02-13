package com.a708.drwa.debate.data.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRoomResponseDto {
    private int debateId;

    @Builder
    public CreateRoomResponseDto(int debateId) {
        this.debateId = debateId;
    }
}
