package com.a708.drwa.game.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameInfo {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameId;

    // 키워드
    @Column(name="keyword", nullable = false)
    @Size(max = 31)
    private String keyword;

    // mvp로 선정된 멤버 아이디
    @Column(name = "mvp_member_id")
    private int mvpMemberId;
    
    @Builder
    public GameInfo(
            String keyword,
            int mvpMemberId
    ) {
        this.keyword = keyword;
        this.mvpMemberId = mvpMemberId;
    }

    // 키워드 수정
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    // MVP 선정 멤버 ID 수정
    public void setMvpMemberId(int mvpMemberId) {
        this.mvpMemberId = mvpMemberId;
    }
}
