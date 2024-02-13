package com.a708.drwa.member.data;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JWTMemberInfo {
    private int memberId;
    private String userId;

    @Builder
    public JWTMemberInfo(int memberId, String userId) {
        this.memberId = memberId;
        this.userId = userId;
    }
}
