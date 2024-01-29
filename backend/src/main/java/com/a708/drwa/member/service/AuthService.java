package com.a708.drwa.member.service;


import com.a708.drwa.member.dto.AuthDto;

/**
 * 인증 관련 서비스 인터페이스
 */
public interface AuthService {
    /**
     * 리프레시 토큰을 저장한다.
     * @param authDto : 리프레시 토큰 정보
     * @param duration : 저장 기간
     */
    void setRefreshToken(AuthDto authDto, long duration);

    /**
     * 리프레시 토큰을 조회한다.
     * @param userId : 사용자 아이디
     * @return : 리프레시 토큰 정보
     */
    AuthDto getRefreshToken(String userId);

    /**
     * 리프레시 토큰을 갱신한다.
     * @param authDto : 리프레시 토큰 정보
     * @param duration : 저장 기간
     */
    void updateRefreshToken(AuthDto authDto, long duration);


}
