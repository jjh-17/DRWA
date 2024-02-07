package com.a708.drwa.member.util;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 토큰을 생성하고 검증하는 컴포넌트
 */
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
     * @param memberId 토큰에 담을 회원의 아이디
     * @param userId 토큰에 담을 회원의 아이디
     * @param expireTime 토큰의 만료시간
     * @return JWT 빌더 객체
     */
    private JwtBuilder createJwtBuilder(int memberId, String userId, long expireTime) {
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .claim("memberId", memberId)
                // 토큰에 담을 정보를 지정한다.
                .claim("userId", userId)
                // 토큰의 발급시간을 지정한다.
                .setIssuedAt(new Date(currentTimeMillis))
                // 토큰의 만료시간을 지정한다.
                .setExpiration(new Date(currentTimeMillis + expireTime))
                // 토큰을 암호화한다.
                .signWith(SignatureAlgorithm.HS256, jwtKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT Access 토큰을 생성한다.
     * @param userId 토큰에 담을 회원의 아이디
     * @return JWT Access 토큰
     */
    public String createAccessToken(int memberId, String userId) {
        return createJwtBuilder(memberId, userId, accessTokenExpireTime * 1000).compact();
    }


    /**
     * JWT Refresh 토큰을 생성한다.
     * @param userId 토큰에 담을 회원의 아이디
     * @return JWT Refresh 토큰
     */
    public String createRefreshToken(int memberId,String userId) {
        return createJwtBuilder(memberId, userId, refreshTokenExpireTime * 1000).compact();
    }

    /**
     * AccessToken에서 멤버 아이디를 추출한다.
     * @param authorization JWT Access 토큰울 담은 Header의 Authorization 값
     * @return 멤버 아이디
     * @throws ParseException 파싱 예외
     */
    public int getMemberId(String authorization) throws ParseException {
        // accessToken을 파싱한다.
        String[] chunks = authorization.split("\\.");
        // Base64.Decoder를 생성한다.
        Base64.Decoder decoder = Base64.getUrlDecoder();

        // payload에 저장된 memberId를 획득한다.
        String payload = new String(decoder.decode(chunks[1]));
        JSONParser parser = new JSONParser();
        // payload를 파싱한다.
        JSONObject obj = (JSONObject) parser.parse(payload);

        // memberId를 추출한다.
        int memberId = (int) obj.get("memberId");

        // memberId를 반환한다.
        return memberId;
    }

    /**
     * AccessToken에서 유저 아이디를 추출한다.
     *
     * @param authorization JWT Access 토큰울 담은 Header의 Authorization 값
     * @return 유저 아이디
     * @throws ParseException 파싱 예외
     */
    public String getUserId(String authorization) throws ParseException {
        // accessToken을 파싱한다.
        String[] chunks = authorization.split("\\.");
        // Base64.Decoder를 생성한다.
        Base64.Decoder decoder = Base64.getUrlDecoder();

        // payload에 저장된 memberId를 획득한다.
        String payload = new String(decoder.decode(chunks[1]));
        JSONParser parser = new JSONParser();
        // payload를 파싱한다.
        JSONObject obj = (JSONObject) parser.parse(payload);

        // memberId를 추출한다.
        String userId = (String) obj.get("userId");

        // memberId를 반환한다.
        return userId;
    }

    /**
     * JWT 토큰의 유효성을 검사한다.
     * @param token 검사할 JWT 토큰
     * @return 유효성 검사 결과
     */
    public boolean validCheck(String token) {
        // 해당 토큰을 확인하면서 예외가 발생하는 경우
        // 만료가 되거나, 잘못된 토큰이다.
        try {
            Jwts.parser()
                    // 토큰을 검증할 때 사용할 key를 지정한다.
                    .setSigningKey(jwtKey.getBytes(StandardCharsets.UTF_8))
                    // 토큰을 파싱한다.
                    .parseClaimsJws(token);

            // 토큰이 유효하다면 true를 반환한다.
            return true;
        } catch (Exception e) {
            // 토큰이 유효하지 않다면 false를 반환한다.
            return false;
        }
    }

}
