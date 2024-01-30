package com.a708.drwa.member.dto;

import com.a708.drwa.member.type.SocialType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
// 알 수 없는 JSON 필드 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoResponse extends SocialUserInfoResponse{


}
