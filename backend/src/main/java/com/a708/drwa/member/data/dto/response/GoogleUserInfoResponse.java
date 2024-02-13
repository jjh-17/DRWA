package com.a708.drwa.member.data.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
// 알 수 없는 JSON 필드 무시
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleUserInfoResponse extends SocialUserInfoResponse{


}
