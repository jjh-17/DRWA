package com.a708.drwa.member.type;

import java.util.Arrays;

public enum SocialType {
    GOOGLE,
    KAKAO,
    NAVER

    /**
     * 문자열로부터 SocialType을 찾아옴
     * @param type SocialType 문자열
     * @return SocialType
     */
    public static SocialType fromString(String type) {
        return Arrays.stream(SocialType.values())
                .filter(socialType -> socialType.name().equalsIgnoreCase(type))
                .findFirst()
                // 지원하지 않는 SocialType일 경우 IllegalArgumentException 발생
                .orElseThrow(() -> new IllegalArgumentException("Unsupported social login type: " + type));
    }
}
