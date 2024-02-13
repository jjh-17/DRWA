package com.a708.drwa.auth.domain;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("refresh")
public class RefreshToken {

    @Id
    private String userId;

    private String refreshToken;

    @TimeToLive
    private long expiredTime;

    @Builder
    public RefreshToken(String userId, String refreshToken, long expiredTime) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiredTime = expiredTime;
    }
}
