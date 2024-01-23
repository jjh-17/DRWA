package com.a708.drwa.profile.service;

import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.dto.request.AddProfileRequest;
import com.a708.drwa.profile.dto.request.UpdateProfileRequest;
import com.a708.drwa.profile.dto.response.ProfileResponse;
import com.a708.drwa.profile.exception.ProfileErrorCode;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final RankService rankService;
    private static final int INIT_POINT = 1000;
    private static final int INIT_MVP_COUNT = 0;

    @Transactional
    public void addProfile(AddProfileRequest addProfileRequest){
        Member member = memberRepository.findById(addProfileRequest.getMemberId())
                .orElseThrow(() -> new GlobalException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        
        Rank INIT_RANK_BRONZE = rankService.findByRankName(RankName.BRONZE);

        String nickname = addProfileRequest.getNickname();
        Profile profile = Profile.builder()
                .nickname(nickname)
                .member(member)
                .point(INIT_POINT)
                .mvpCount(INIT_MVP_COUNT)
                .rank(INIT_RANK_BRONZE)
                .build();
        profileRepository.save(profile);
    }

    @Transactional
    public void updateProfile(UpdateProfileRequest updateProfileRequest){
        Integer profileId = updateProfileRequest.getProfileId();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND));

        profile.updateProfile(updateProfileRequest);
    }

    public ProfileResponse findProfileByMemberId(Integer memberId){
        Profile profile = profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND));

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .memberId(memberId)
                .nickname(profile.getNickname())
                .point(profile.getPoint())
                .rankName(profile.getRank().getRankName())
                .build();
    }

    public List<Profile> findAll(){
        return profileRepository.findAll();
    }
    public List<ProfileResponse> findAllWithDto(){
        return profileRepository.findAll()
                .stream()
                .map(p -> ProfileResponse.builder()
                        .profileId(p.getId())
                        .memberId(p.getMember().getId())
                        .point(p.getPoint())
                        .mvpCount(p.getMvpCount())
                        .nickname(p.getNickname())
                        .rankName(p.getRank().getRankName())
                        .build())
                .collect(Collectors.toList());
    }


}
