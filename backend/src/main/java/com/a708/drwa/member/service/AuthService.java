package com.a708.drwa.member.service;

import com.a708.drwa.member.exception.TokenValidationException;
import com.a708.drwa.member.util.JWTUtil;
import com.a708.drwa.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 서비스 구현체
 */
@Service
public class AuthService {
    @Value("${jwt.accesstoken.expiretime}")
    private int accessTokenExpireTime;

    @Value("${jwt.refreshtoken.expiretime}")
    private int refreshTokenExpireTime;

    private final RedisUtil redisUtil;
    private final JWTUtil jwtUtil;

    // 생성자를 통해 의존성 주입
    public AuthService(RedisUtil redisUtil, JWTUtil jwtUtil) {
        this.redisUtil = redisUtil;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param userId 사용자 아이디
     * @return 새로 발급한 액세스 토큰
     */
    public String silentRefreshProcess(String userId) {
        // 사용자 아이디를 키로 리프레시 토큰을 조회한다.
        String refreshToken = getRefreshToken(userId);

        // 리프레시 토큰이 없는 경우
        if (refreshToken == null) {
            // 401 Unauthorized 에러를 발생시킨다.
            throw new TokenValidationException("Refresh token not found", HttpStatus.UNAUTHORIZED);
        }

        // 리프레시 토큰이 유효한지 검사한다.
        if (!jwtUtil.validCheck(refreshToken)) {
            // 리프레시 토큰이 유효하지 않으면 403 Forbidden 에러를 발생시킨다.
            throw new TokenValidationException("Invalid refresh token", HttpStatus.FORBIDDEN);
        }

        // 새로운 액세스 토큰을 발급한다.
        String newAccessToken = jwtUtil.createAccessToken(userId);

        // 새로운 리프레시 토큰을 발급한다.
        String newRefreshToken = jwtUtil.createRefreshToken(userId);
        // 발급한 리프레시 토큰을 저장한다.
        updateRefreshToken(userId, newRefreshToken, refreshTokenExpireTime);

        // 새로 발급한 액세스 토큰을 반환한다.
        return newAccessToken;
    }

    /**
     * 리프레시 토큰을 Redis에 저장 및 갱신한다.
     * @param userId 사용자 아이디
     * @param refreshToken 리프레시 토큰
     * @param duration 저장 기간
     */
    public void updateRefreshToken(String userId, String refreshToken, long duration) {
        // 사용자 아이디를 키로 리프레시 토큰을 저장한다.
        // setData(키, 값, 저장 기간)
        redisUtil.setData(userId, refreshToken, duration);
    }

    /**
     * 리프레시 토큰을 Redis에서 조회한다.
     * @param userId 사용자 아이디
     * @return 리프레시 토큰
     */
    public String getRefreshToken(String userId) {
        return redisUtil.getData(userId);
    }

}
