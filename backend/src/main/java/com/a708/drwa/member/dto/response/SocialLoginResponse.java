package com.a708.drwa.member.dto.response;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 소셜 로그인 응답 DTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SocialLoginResponse {
    private int memberId;
    private String userId;
    private String accessToken;
    private List<DebateCategory> interests; // 사용자의 관심 카테고리 목록

}
