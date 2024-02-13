package com.a708.drwa.profile.service;

import com.a708.drwa.annotation.AuthRequired;
import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.profile.domain.ProfileImage;
import com.a708.drwa.profile.repository.ProfileImageRepository;
import com.a708.drwa.utils.JWTUtil;
import com.a708.drwa.utils.S3Uploader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final S3Uploader s3Uploader;

    @Transactional
    public String uploadProfileImage(MultipartFile file, String token) throws IOException {
        // 멤버 찾기
        JWTMemberInfo memberInfo = jwtUtil.getMember(token);
        Member member = memberRepository.findById(memberInfo.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 파일을 S3에 업로드하고, URL을 받아옴
        String s3Url = s3Uploader.upload(file, "profile-images");

        // 멤버 ID에 해당하는 기존 프로필 이미지가 있는지 확인
        ProfileImage existingProfileImage = profileImageRepository.findByMemberId(member.getId());

        if (existingProfileImage != null) {
            // S3에서 기존 이미지 파일 삭제
            try {
                s3Uploader.deleteS3(existingProfileImage.getS3Path());
            } catch (Exception e) {
                log.error("Error deleting old profile image from S3", e);
            }
        }

        // 업로드된 이미지의 메타데이터를 데이터베이스에 저장
        profileImageRepository.save(new ProfileImage(member, file.getOriginalFilename(), s3Url));

        return s3Url; // 업로드된 이미지의 URL 반환
    }

}
