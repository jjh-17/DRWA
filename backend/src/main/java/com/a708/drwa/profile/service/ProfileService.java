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
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.rank.service.RankService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.a708.drwa.redis.constant.Constants.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final RankService rankService;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
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

        RankingMember rankingMember = RankingMember.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                //.title(profile.getTitle())
                //.winRate()
                .point(profile.getPoint())
                .build();

        // Save Points with each category
        savePointsInRedis(rankingMember, profile);

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

        Set<RankingMember> members = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);

        if(members == null) {
            throw new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND);
        }

        RankingMember memberRankInfo = members
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

    public List<RankingMember> findAll(){
        Set<RankingMember> members = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);
        if(members == null){
            return List.of();
        }

        return members.stream().toList();
    }

    public List<ProfileResponse> findAllWithDto(){
        Set<RankingMember> rankingMembers = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);
        if(rankingMembers == null){
            new GlobalException(MemberErrorCode.EXAMPLE_ERROR_CODE);
        }

        List<Profile> profiles = profileRepository.findAll();

        for(RankingMember rankingMember : rankingMembers){
            Optional<Profile> matchingProfile = profiles.stream()
                    .filter(p -> p.getMember().getId().equals(rankingMember.getMemberId()))
                    .findFirst();
            matchingProfile.ifPresent(profile -> profile.updatePoint(rankingMember.getPoint()));
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
    private void savePointsInRedis(RankingMember rankingMember, Profile profile) {
        // Total Point
        rankMemberRedisTemplate.opsForZSet().add(RANK_REDIS_KEY, rankingMember, -profile.getPoint());

        // Each Category Point
        rankMemberRedisTemplate.opsForZSet().add(RANK_ANIMAL_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_CULTURE_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_ECONOMY_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_FOOD_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_LOVE_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_PERSON_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_SHOPPING_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_POLITICS_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_SOCIAL_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_SPORTS_REDIS_KEY, rankingMember, -profile.getPoint());
        rankMemberRedisTemplate.opsForZSet().add(RANK_ETC_REDIS_KEY, rankingMember, -profile.getPoint());
    }


}
