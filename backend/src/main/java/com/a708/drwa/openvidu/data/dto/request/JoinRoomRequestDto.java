package com.a708.drwa.openvidu.data.dto.request;

import com.a708.drwa.debate.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRoomRequestDto {
    private String sessionId;
    private String nickName;
    private Role role;
}
