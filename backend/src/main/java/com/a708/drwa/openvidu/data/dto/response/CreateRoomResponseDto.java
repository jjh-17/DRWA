package com.a708.drwa.openvidu.data.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRoomResponseDto {
    private String sessionId;

    @Builder
    public CreateRoomResponseDto(String sessionId) {
        this.sessionId = sessionId;
    }
}
