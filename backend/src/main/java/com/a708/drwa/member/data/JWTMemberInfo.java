package com.a708.drwa.member.data;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class JWTMemberInfo {
    private int memberId;
    private String userId;

    @Builder
    public JWTMemberInfo(int memberId, String userId) {
        this.memberId = memberId;
        this.userId = userId;
    }
}
