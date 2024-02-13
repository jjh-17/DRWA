package com.a708.drwa.profile.domain;

import com.a708.drwa.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_image")
@NoArgsConstructor
@Getter
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @Column(name = "original_path")
    private String originalPath;

    @Setter
    @Column(name = "s3_path")
    private String s3Path;

    public ProfileImage(Member member, String originalFilename, String s3Url) {
        this.member = member;
        this.originalPath = originalFilename;
        this.s3Path = s3Url;
    }
}
