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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int gameId;

    @Column(name="keyword", nullable = false)
    @Size(max = 31)
    private String keyword;

    @Column(name = "mvp_member_id", nullable = false)
    private int mvpMemberId;

    @Builder
    public GameInfo(
            String keyword,
            int mvpMemberId
    ) {
        this.keyword = keyword;
        this.mvpMemberId = mvpMemberId;
    }

}
