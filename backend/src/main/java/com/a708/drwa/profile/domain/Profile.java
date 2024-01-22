package com.a708.drwa.profile.domain;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.profile.dto.request.UpdateProfileRequest;
import com.a708.drwa.rank.domain.Rank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    @Id @GeneratedValue
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rank_id")
    private Rank rank;

    private String nickname;
    private String point;
    private String mvpCount;

    /**
     * TODO
     * add profile image
     *
     */

    @Builder
    private Profile(Member member, String nickname, String point, String mvpCount, Rank rank) {
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
