package com.a708.drwa.game.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @Column(name = "is_private")
    private boolean isPrivate;

    @Builder
    public GameInfo(
            String keyword,
            int mvpMemberId,
            boolean isPrivate
    ) {
        this.keyword = keyword;
        this.mvpMemberId = mvpMemberId;
        this.isPrivate = isPrivate;
    }
}
