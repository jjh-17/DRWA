package com.a708.drwa.debate.data;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.enums.Role;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DebateMember implements Serializable {
    private int memberId;
    private String nickName;
    private Role role;

    @Builder
    public DebateMember(int memberId, String nickName, Role role) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.role = role;
    }
}