package com.a708.drwa.auth.service;

import com.a708.drwa.auth.domain.RefreshToken;
import com.a708.drwa.auth.exception.AuthErrorCode;
import com.a708.drwa.auth.exception.AuthException;
import com.a708.drwa.auth.repository.AuthRepository;
import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.utils.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 인증 관련 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.refreshtoken.expiretime}")
    private int refreshTokenExpireTime;

    private final AuthRepository authRepository;
    private final JWTUtil jwtUtil;


    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param token header에 저장된 토큰
     * @return newAccessToken 새로운 access Token
     */
    public String silentRefreshProcess(String token) {
        // 토큰이 없거나 잘못된 형식인 경우 에러
        if(token == null || !token.startsWith("Bearer "))
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_ERROR);

        // get refresh token
        token = token.substring(7);

        // 토큰에서 사용자 정보를 뽑아낸다.
        JWTMemberInfo memberInfo = jwtUtil.getMember(token);

        // 사용자 아이디를 키로 토큰 조회
        RefreshToken refreshToken = getRefreshToken(memberInfo.getUserId());

        // 리프레시 토큰이 유효한지 검사한다.
        jwtUtil.validCheck(refreshToken.getRefreshToken());

        // 새로운 액세스 토큰을 발급한다.
        String newAccessToken = jwtUtil.createAccessToken(memberInfo);
        // 새로운 리프레시 토큰을 발급한다.
        String newRefreshToken = jwtUtil.createRefreshToken(memberInfo);
        // 발급한 리프레시 토큰을 저장한다.
        updateRefreshToken(memberInfo.getUserId(), newRefreshToken, refreshTokenExpireTime);

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
        authRepository.save(RefreshToken.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .expiredTime(duration)
                .build());
    }

    /**
     * 리프레시 토큰을 Redis에서 조회한다.
     * @param userId 사용자 아이디
     * @return 리프레시 토큰
     */
    public RefreshToken getRefreshToken(String userId) {
        return authRepository.findById(userId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.TOKEN_NOT_EXIST_ERROR));
    }

}
