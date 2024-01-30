package com.a708.drwa.member.dto;

import com.a708.drwa.member.type.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
// 알 수 없는 JSON 필드 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocialUserInfoResponse {
    private String id;
    private String email;
    private String name;
    private String picture;
    private SocialType socialType;

    public void setSocialType(SocialType socialType) {
        this.socialType = socialType;
    }
}
