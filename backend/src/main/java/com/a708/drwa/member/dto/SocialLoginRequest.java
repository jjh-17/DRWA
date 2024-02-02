package com.a708.drwa.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class SocialLoginRequest {
    private String socialType;
    private String code;


}
