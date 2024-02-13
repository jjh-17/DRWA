package com.a708.drwa.member.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class SocialLoginRequest {
    private String socialType;
    private String code;


}
