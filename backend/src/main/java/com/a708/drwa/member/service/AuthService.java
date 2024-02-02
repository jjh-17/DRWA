package com.a708.drwa.member.service;

import com.a708.drwa.member.dto.AuthDto;
import com.a708.drwa.redis.util.RedisUtil;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 서비스 구현체
 */
@Service
public class AuthService {

    private final RedisUtil redisUtil;

    // RedisUtil을 주입받는다.
    public AuthService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    /**
     * 리프레시 토큰을 저장한다.
     * @param authDto : 리프레시 토큰 정보
     * @param duration : 저장 기간
     */
    public void setRefreshToken(AuthDto authDto, long duration) {
        // 사용자 아이디를 키로 리프레시 토큰을 저장한다.
        // setData(키, 값, 저장 기간)
        redisUtil.set(authDto.getUserId(), authDto.getRefreshToken(), duration);
    }

    /**
     * 리프레시 토큰을 조회한다.
     * @param userId : 사용자 아이디
     * @return : 리프레시 토큰 정보
     */
    public AuthDto getRefreshToken(String userId) {
        return new AuthDto(userId, redisUtil.getData(userId));
    }

    /**
     * 리프레시 토큰을 갱신한다.
     * @param authDto : 리프레시 토큰 정보
     * @param duration : 저장 기간
     */
    public void updateRefreshToken(AuthDto authDto, long duration) {
        // setData(키, 값, 저장 기간)
        redisUtil.set(authDto.getUserId(), authDto.getRefreshToken(), duration);
    }
}
