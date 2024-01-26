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
import com.a708.drwa.rank.redis.RankMember;
import com.a708.drwa.rank.service.RankService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final RankService rankService;
    private final RedisTemplate<String, RankMember> rankMemberRedisTemplate;
    private static final int INIT_POINT = 1000;
    private static final int INIT_MVP_COUNT = 0;
    private static final String REDIS_KEY = "rank";

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

        RankMember rankMember = RankMember.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                //.title(profile.getTitle())
                //.winRate()
                .point(profile.getPoint())
                .build();

        rankMemberRedisTemplate.opsForZSet().add(REDIS_KEY, rankMember, profile.getPoint());
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

        Set<RankMember> members = rankMemberRedisTemplate.opsForZSet().range(REDIS_KEY, 0, -1);

        if(members == null) {
            throw new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND);
        }

        RankMember memberRankInfo = members
                .stream()
                .filter(rankMember -> rankMember.getMemberId().equals(memberId))
                .findAny()
                .orElseThrow(() -> new GlobalException(MemberErrorCode.EXAMPLE_ERROR_CODE));

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .memberId(memberId)
                .nickname(profile.getNickname())
                .point(memberRankInfo.getPoint())
                .rankName(profile.getRank().getRankName())
                .build();
    }

    public List<RankMember> findAll(){
        Set<RankMember> members = rankMemberRedisTemplate.opsForZSet().range(REDIS_KEY, 0, -1);
        if(members == null){
            return List.of();
        }

        return members.stream().toList();
    }
    public List<ProfileResponse> findAllWithDto(){
        Set<RankMember> rankMembers = rankMemberRedisTemplate.opsForZSet().range(REDIS_KEY, 0, -1);
        if(rankMembers == null){
            new GlobalException(MemberErrorCode.EXAMPLE_ERROR_CODE);
        }

        List<Profile> profiles = profileRepository.findAll();

        for(RankMember rankMember : rankMembers){
            Optional<Profile> matchingProfile = profiles.stream()
                    .filter(p -> p.getMember().getId().equals(rankMember.getMemberId()))
                    .findFirst();
            matchingProfile.ifPresent(profile -> profile.updatePoint(rankMember.getPoint()));
        }

        return profiles
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
