package com.a708.drwa.profile.service;

import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Result;
import com.a708.drwa.game.repository.RecordRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.a708.drwa.redis.constant.Constants.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;
    private final RecordRepository recordRepository;
    private final RankService rankService;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private static final int INIT_POINT = 1000;
    private static final int INIT_MVP_COUNT = 0;

    @Transactional
    public void addProfile(AddProfileRequest addProfileRequest){
        Member member = memberRepository.findById(addProfileRequest.getMemberId())
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));
        
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
                .achievement("뉴비")
                .winRate(0)
                .point(profile.getPoint())
                .build();
        
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

        List<Record> records = recordRepository.findByMemberId(memberId);

        int winCount = getCount(records, Result.WIN);
        int loseCount = getCount(records, Result.LOSE);
        int tieCount = getCount(records, Result.TIE);

        int winRate = 0;
        if(winCount + loseCount != 0)
            winRate = (int) ((double) winCount / (winCount + loseCount + tieCount) * 100.0);

        RankingMember memberRankInfo = members
                .stream()
                .filter(rankMember -> rankMember.getMemberId().equals(memberId))
                .findAny()
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        return ProfileResponse.builder()
                .profileId(profile.getId())
                .memberId(memberId)
                .nickname(profile.getNickname())
                .point(memberRankInfo.getPoint())
                .rankName(profile.getRank().getRankName())
                .winCount(winCount)
                .loseCount(loseCount)
                .tieCount(tieCount)
                .winRate(winRate)
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
        List<ProfileResponse> results = new ArrayList<>();

        Set<RankingMember> rankingMembers = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);
        if(rankingMembers == null){
            throw new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND);
        }

        List<Profile> profiles = profileRepository.findAll();

        for(RankingMember rankingMember : rankingMembers){ // SortedSet
            Optional<Profile> matchingProfile = profiles.stream()
                    .filter(p -> p.getMember().getId().equals(rankingMember.getMemberId()))
                    .findFirst(); // SortedSet에 들어있는 각 Member에 해당하는 Profile
            matchingProfile.ifPresent(profile -> {
                profile.updatePoint(rankingMember.getPoint()); // SortedSet에 있는 포인트 반영

                // 승,패, 승률 가져옴
                List<Record> records = recordRepository.findByMemberId(rankingMember.getMemberId());

                int winCount = getCount(records, Result.WIN);
                int loseCount = getCount(records, Result.LOSE);
                int tieCount = getCount(records, Result.TIE);

                int winRate = 0;
                if(winCount + loseCount != 0)
                    winRate = (int) ((double) winCount / (winCount + loseCount + tieCount) * 100.0);

                ProfileResponse profileResponse = ProfileResponse.builder()
                        .profileId(profile.getId())
                        .memberId(profile.getMember().getId())
                        .point(profile.getPoint())
                        .mvpCount(profile.getMvpCount())
                        .nickname(profile.getNickname())
                        .rankName(profile.getRank().getRankName())
                        .winCount(winCount)
                        .loseCount(loseCount)
                        .tieCount(tieCount)
                        .winRate(winRate)
                        .build();
                results.add(profileResponse);
            });
        }

        return results;
    }

    private int getCount(List<Record> records, Result result) {
        return (int) records.stream()
                .filter(record -> record.getResult().equals(result))
                .count();
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
