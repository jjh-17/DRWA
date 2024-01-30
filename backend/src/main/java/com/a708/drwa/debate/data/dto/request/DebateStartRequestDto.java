package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.data.dto.DebateMemberDto;
import com.a708.drwa.debate.data.dto.RoomInfo;
import lombok.*;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DebateStartRequestDto {
    private int debateId;
    private RoomInfo roomInfo;
    private HashMap<Integer, DebateMemberDto> memberDtoHashMap;
}
