package com.a708.drwa.debate.data;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.enums.Role;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DebateMember {
    private int memberId;
    private String nickName;
    private Role role;
}
