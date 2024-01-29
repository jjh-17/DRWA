package com.a708.drwa.member.service;


import com.a708.drwa.member.dto.AuthDto;

public interface AuthService {
    void setRefreshToken(AuthDto authDto, long duration);
    AuthDto getRefreshToken(String userId);
    void updateRefreshToken(AuthDto authDto, long duration);


}
