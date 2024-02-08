package com.a708.drwa.member.service;

import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.util.JWTUtil;
import com.a708.drwa.redis.domain.MemberRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 인증 관련 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${jwt.accesstoken.expiretime}")
    private int accessTokenExpireTime;

    @Value("${jwt.refreshtoken.expiretime}")
    private int refreshTokenExpireTime;

    private final RedisTemplate<String, Object> redisTemplate;
    private final JWTUtil jwtUtil;
    private final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();


    /**
     * 리프레시 토큰을 받아서 유효성 검증 후, 새로운 액세스 토큰을 발급한다.
     * @param token 엑세스토큰
     * @return 새로 발급한 액세스 토큰
     */
    public String silentRefreshProcess(String token) throws ParseException {
        int memberId = jwtUtil.getMemberId(token);
        String userId = jwtUtil.getUserId(token);

        // 사용자 아이디를 키로 리프레시 토큰을 조회한다.
        String refreshToken = getRefreshToken(userId);

        // 리프레시 토큰이 없는 경우
        if (refreshToken == null) {
            // 401 Unauthorized 에러를 발생시킨다.
            throw new MemberException(MemberErrorCode.TOKEN_NOT_FOUND);
        }

        // 리프레시 토큰이 유효한지 검사한다.
        if (!jwtUtil.validCheck(refreshToken)) {
            // 리프레시 토큰이 유효하지 않으면 403 Forbidden 에러를 발생시킨다.
            throw new MemberException(MemberErrorCode.INVALID_TOKEN);
        }

        // 새로운 액세스 토큰을 발급한다.
        String newAccessToken = jwtUtil.createAccessToken(memberId, userId);

        // 새로운 리프레시 토큰을 발급한다.
        String newRefreshToken = jwtUtil.createRefreshToken(memberId, userId);
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
        HashOperations<String, MemberRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.put(userId, MemberRedisKey.REFRESH_TOKEN, refreshToken);
        redisTemplate.expire(userId, duration, TimeUnit.DAYS);
    }

    /**
     * 리프레시 토큰을 Redis에서 조회한다.
     * @param userId 사용자 아이디
     * @return 리프레시 토큰
     */
    public String getRefreshToken(String userId) {
        HashOperations<String, MemberRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        Object result = hashOperations.get(userId, MemberRedisKey.REFRESH_TOKEN);
        if(result == null) throw new MemberException(MemberErrorCode.TOKEN_NOT_FOUND);
        return (String) result;
    }

}
