package com.a708.drwa.profile.service;

import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.profile.domain.ProfileImage;
import com.a708.drwa.profile.repository.ProfileImageRepository;
import com.a708.drwa.utils.JWTUtil;
import com.a708.drwa.utils.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileImageService {

    private final ProfileImageRepository profileImageRepository;
    private final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;
    private final S3Uploader s3Uploader;

    /**
     * 프로필 이미지를 업로드하고, 이미지 URL을 반환한다.
     *
     * @param file  : 업로드할 이미지 파일
     * @param token : JWT 토큰
     * @return : 업로드된 이미지 URL
     */
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

        // 기존 프로필 이미지가 있는 경우, 기존 이미지 정보 업데이트
        if (existingProfileImage != null) {
            // S3에서 기존 이미지 파일 삭제
            try {
                s3Uploader.deleteS3(existingProfileImage.getS3Path());
            } catch (Exception e) {
                log.error("Error deleting old profile image from S3", e);
            }

            // 기존 이미지 정보 업데이트
            existingProfileImage.setOriginalPath(file.getOriginalFilename());
            existingProfileImage.setS3Path(s3Url);
        } else { // 기존 프로필 이미지가 없는 경우, 새 프로필 이미지 정보 저장
            // 새 프로필 이미지 정보 저장
            existingProfileImage = new ProfileImage(member, file.getOriginalFilename(), s3Url);
            profileImageRepository.save(existingProfileImage);
        }


        return s3Url; // 업로드된 이미지의 URL 반환
    }

    /**
     * 멤버 ID를 사용하여 프로필 이미지 URL을 조회한다.
     *
     * @param memberId : 멤버 ID
     * @return : 프로필 이미지 URL
     */
    public String findProfileImageUrlByMemberId(int memberId) {
        // 멤버 ID를 사용하여 프로필 이미지 정보 조회
        ProfileImage profileImage = profileImageRepository.findByMemberId(memberId);

        // 프로필 이미지가 존재하는 경우, 이미지 URL 반환
        if (profileImage != null) {
            return profileImage.getS3Path();
        }

        // 프로필 이미지가 없는 경우, null 또는 기본 이미지 URL 반환
        return null; // 또는 기본 이미지 URL을 반환할 수 있습니다.
    }

}
