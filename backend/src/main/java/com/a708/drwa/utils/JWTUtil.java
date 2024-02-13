package com.a708.drwa.utils;


import com.a708.drwa.auth.exception.AuthErrorCode;
import com.a708.drwa.auth.exception.AuthException;
import com.a708.drwa.member.data.JWTMemberInfo;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 토큰을 생성하고 검증하는 컴포넌트
 */
@Slf4j
@Component
public class JWTUtil {

    @Value("${jwt.key}")
    private String jwtKey;

    @Value("${jwt.accesstoken.expiretime}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refreshtoken.expiretime}")
    private Long refreshTokenExpireTime;

    /**
     * JWT 빌더 객체를 생성한다.
     * @param memberInfo memberId, userId
     * @param expireTime 갱신 시간
     * @return JWT
     */
    private JwtBuilder createJwtBuilder(JWTMemberInfo memberInfo, long expireTime) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("--------------------------- create JWT Token ---------------------------");
        log.info("curTime : {}, expTime : {}", currentTimeMillis, currentTimeMillis + expireTime);
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                // 토큰에 담을 정보를 지정한다.
                .claim("memberId", memberInfo.getMemberId())
                .claim("userId", memberInfo.getUserId())
                // 토큰의 발급시간을 지정한다.
                .setIssuedAt(new Date(currentTimeMillis))
                // 토큰의 만료시간을 지정한다.
                .setExpiration(new Date(currentTimeMillis + expireTime))
                // 토큰을 암호화한다.
                .signWith(SignatureAlgorithm.HS256, jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT Access 토큰을 생성한다.
     * @param memberInfo 토큰에 담을 회원의 정보
     * @return JWT Access 토큰
     */
    public String createAccessToken(JWTMemberInfo memberInfo) {
        return createJwtBuilder(memberInfo, accessTokenExpireTime * 1000).compact();
    }


    /**
     * JWT Refresh 토큰을 생성한다.
     * @param memberInfo 토큰에 담을 회원의 정보
     * @return JWT Refresh 토큰
     */
    public String createRefreshToken(JWTMemberInfo memberInfo) {
        return createJwtBuilder(memberInfo, refreshTokenExpireTime * 1000).compact();
    }

    /**
     * AccessToken에서 멤버 아이디를 추출한다.
     * @param accessToken JWT Access 토큰울 담은 Header의 Authorization 값
     * @return 멤버 정보
     * @throws AuthException 권한 정보 없음
     */
    public JWTMemberInfo getMember(String accessToken) {
        Claims claims = parseClaims(accessToken);

        Integer memberId = (Integer) claims.get("memberId");
        String userId = (String) claims.get("userId");
        log.info(">>>>>>>> USER INFO  {} : {}", memberId, userId);

        if(memberId == null || userId == null)
            throw new AuthException(AuthErrorCode.NO_INFO_IN_TOKEN_ERROR);

        // MemberInfo를 반환한다.
        return JWTMemberInfo.builder()
                .memberId(memberId)
                .userId(userId)
                .build();
    }

    /**
     * JWT 토큰의 유효성을 검사한다.
     * @param token
     */
    public void validCheck(String token) {
        // 해당 토큰을 확인하면서 예외가 발생하는 경우
        // 만료가 되거나, 잘못된 토큰이다.
        try {
            Jwts.parser()
                    .setSigningKey(jwtKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token);
            log.info("this token is valid !!");
        } catch (SecurityException | MalformedJwtException e) {
            // 유효하지 않은 토큰
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_ERROR);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN_ERROR);
        } catch (UnsupportedJwtException e) {
            // 지원하지 않는 토큰
            throw new AuthException(AuthErrorCode.UNSUPPORTED_TOKEN_ERROR);
        } catch (IllegalArgumentException e) {
            // 비어있는 토큰
            throw new AuthException(AuthErrorCode.NO_INFO_IN_TOKEN_ERROR);
        }
    }

    public Claims parseClaims(String authToken) {
        if(authToken == null || !authToken.startsWith("Bearer"))
            throw new AuthException(AuthErrorCode.INVALID_TOKEN_ERROR);

        authToken = authToken.replace("Bearer ", "").trim();
        log.info("parsed authToken >>>>>>> {}", authToken);
        try {
            return Jwts.parser()
                    .setSigningKey(jwtKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(authToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
