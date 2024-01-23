package com.a708.drwa.profile.domain;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.profile.dto.request.UpdateProfileRequest;
import com.a708.drwa.rank.domain.Rank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    private String nickname;
    private int point;
    private int mvpCount;

    /**
     * TODO
     * add profile image
     *
     */

    @Builder
    private Profile(Member member, String nickname, int point, int mvpCount, Rank rank) {
        this.member = member;
        this.nickname = nickname;
        this.point = point;
        this.mvpCount = mvpCount;
        this.rank = rank;
    }

    public void updateProfile(UpdateProfileRequest request) {
        this.nickname = request.getNickname();
    }
}
