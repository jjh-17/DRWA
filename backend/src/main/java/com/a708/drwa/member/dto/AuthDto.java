package com.a708.drwa.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@ToString
public class AuthDto {
    private String userId;
    private String refreshToken;




}
