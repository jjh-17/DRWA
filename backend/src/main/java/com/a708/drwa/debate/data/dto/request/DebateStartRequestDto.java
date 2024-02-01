package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.RoomInfo;
import lombok.*;

import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DebateStartRequestDto {
    private int debateId;
    private RoomInfo roomInfo;
    private HashMap<Integer, DebateMember> memberDtoHashMap;
}
