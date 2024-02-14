package com.a708.drwa.member.data.dto.response;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.data.dto.InterestDto;
import com.a708.drwa.member.domain.MemberInterest;
import com.a708.drwa.profile.dto.response.ProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 소셜 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
public class SocialLoginResponse {
    private int memberId;
    private String userId;
    private String accessToken;
    private List<InterestDto> interests; // 사용자의 관심 카테고리 목록
    private ProfileResponse profile; // 사용자의 프로필 정보
    private String profileImageUrl;

    @Builder
    public SocialLoginResponse(
            int memberId,
            String userId,
            String accessToken,
            List<InterestDto> interests,
            ProfileResponse profile,
            String profileImageUrl) {
        this.memberId = memberId;
        this.userId = userId;
        this.accessToken = accessToken;
        this.interests = interests;
        this.profile = profile;
        this.profileImageUrl = profileImageUrl;

    }
}
