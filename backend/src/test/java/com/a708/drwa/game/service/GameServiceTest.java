package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.VoteInfo;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.repository.RankRepository;
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.redis.constant.Constants;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.domain.MemberRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameServiceTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RedisTemplate<String, RankingMember> rankingMemberRedisTemplate;
    @Autowired DebateRepository debateRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired RankRepository rankRepository;
    @Autowired GameService gameService;
    RedisKeyUtil redisKeyUtil = new RedisKeyUtil();


    @BeforeEach
    void before() {
        HashOperations<String, DebateRedisKey, Object> debateHashOperations = redisTemplate.opsForHash();
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = redisTemplate.opsForHash();
        ZSetOperations<String, RankingMember> rankingMemberZSetOperations = rankingMemberRedisTemplate.opsForZSet();
        Constants constants = new Constants();

        // Debate 생성 1
        Debate debate = Debate.builder().debateCategory(DebateCategory.ANIMAL).build();
        String debateRedisKey = debate.getDebateId()+"";
        debateRepository.save(Debate.builder().debateCategory(DebateCategory.ANIMAL).build());

        // 멤버 생성 8
        Member member1 = Member.builder().userId("userId1").socialType(SocialType.KAKAO).build();
        Member member2 = Member.builder().userId("userId2").socialType(SocialType.KAKAO).build();
        Member member3 = Member.builder().userId("userId3").socialType(SocialType.KAKAO).build();
        Member member4 = Member.builder().userId("userId4").socialType(SocialType.KAKAO).build();
        Member member5 = Member.builder().userId("userId5").socialType(SocialType.KAKAO).build();
        Member member6 = Member.builder().userId("userId6").socialType(SocialType.KAKAO).build();
        Member member7 = Member.builder().userId("userId7").socialType(SocialType.KAKAO).build();
        Member member8 = Member.builder().userId("userId8").socialType(SocialType.KAKAO).build();
        memberRepository.saveAll(List.of(member1, member2, member3, member4, member5, member6, member7, member8));

        // 랭크 생성 1
        Rank rank = Rank.builder().rankName(RankName.DIAMOND).pivotPoint(1).build();
        rankRepository.save(rank);

        // 프로필 생성 8
        Profile profile1 = Profile.builder()
                .nickname("nickname1").member(member1).point(1).mvpCount(1).rank(rank).build();
        Profile profile2 = Profile.builder()
                .nickname("nickname2").member(member2).point(2).mvpCount(2).rank(rank).build();
        Profile profile3 = Profile.builder()
                .nickname("nickname3").member(member3).point(3).mvpCount(3).rank(rank).build();
        Profile profile4 = Profile.builder()
                .nickname("nickname4").member(member4).point(4).mvpCount(4).rank(rank).build();
        Profile profile5 = Profile.builder()
                .nickname("nickname5").member(member5).point(5).mvpCount(5).rank(rank).build();
        Profile profile6 = Profile.builder()
                .nickname("nickname6").member(member6).point(6).mvpCount(6).rank(rank).build();
        Profile profile7 = Profile.builder()
                .nickname("nickname7").member(member7).point(7).mvpCount(7).rank(rank).build();
        Profile profile8 = Profile.builder()
                .nickname("nickname8").member(member8).point(8).mvpCount(8).rank(rank).build();
        profileRepository.saveAll(List.of(profile1, profile2, profile3, profile4, profile5, profile6, profile7, profile8));

        // RoomInfo 생성
        RoomInfo roomInfo = RoomInfo.builder()
                .debateCategory(debate.getDebateCategory().name())
                .hostId(member1.getId())
                .debateTitle("privateTitle")
                .leftKeyword("privateLeft").rightKeyword("privateRight")
                .playerNum(4).jurorNum(3)
//                .isPrivate(true).password("privatePwd")
                .isPrivate(false).password(null)
                .speakingTime(100).readyTime(300).qnaTime(200)
                .build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.ROOM_INFO, roomInfo);

        // DebateMembers 생성
        DebateMember debateMember1 = DebateMember.builder()
                .memberId(member1.getId()).nickName(profile1.getNickname()).role(Role.A_TEAM).build();
        DebateMember debateMember2 = DebateMember.builder()
                .memberId(member2.getId()).nickName(profile2.getNickname()).role(Role.A_TEAM).build();
        DebateMember debateMember3 = DebateMember.builder()
                .memberId(member3.getId()).nickName(profile3.getNickname()).role(Role.B_TEAM).build();
        DebateMember debateMember4 = DebateMember.builder()
                .memberId(member4.getId()).nickName(profile4.getNickname()).role(Role.B_TEAM).build();
        DebateMember debateMember5 = DebateMember.builder()
                .memberId(member5.getId()).nickName(profile5.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember6 = DebateMember.builder()
                .memberId(member6.getId()).nickName(profile6.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember7 = DebateMember.builder()
                .memberId(member7.getId()).nickName(profile7.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember8 = DebateMember.builder()
                .memberId(member8.getId()).nickName(profile8.getNickname()).role(Role.WATCHER).build();
        DebateMembers debateMembers = DebateMembers.builder()
                .leftMembers(List.of(debateMember1, debateMember2))
                .rightMembers(List.of(debateMember3, debateMember4))
                .jurors(List.of(debateMember5, debateMember6, debateMember7))
                .watchers(List.of(debateMember8))
                .build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.DEBATE_MEMBER_LIST, debateMembers);

        // VoteInfo 생성
        VoteInfo voteInfo = VoteInfo.builder().leftVote(2).rightVote(1).build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.VOTE_INFO, voteInfo);

        // MVP_MAP 생성
        Map<String, Integer> mvpMap = new HashMap<>();
        mvpMap.put(member5.getId()+"", member1.getId());
        mvpMap.put(member6.getId()+"", member1.getId());
        mvpMap.put(member7.getId()+"", member4.getId());
        debateHashOperations.put(debateRedisKey, DebateRedisKey.MVP, mvpMap);

        // 배심원의 VOTE_MAP 생성
        Map<String, String> voteMap = new HashMap<>();
        voteMap.put(member5.getId()+"", Team.A.string());
//        voteMapA.put(member6.getId()+"", Team.A.string());
        voteMap.put(member7.getId()+"", Team.B.string());

        debateHashOperations.put(debateRedisKey, DebateRedisKey.VOTE_MAP, voteMap);

        // redis_ranking 생성
        createRankingMember(member1.getId(), profile1.getNickname(), profile1.getPoint(), getWinRate(1, 1, 1),
                profile1.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member2.getId(), profile2.getNickname(), profile2.getPoint(), getWinRate(2, 2, 2),
                profile2.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member3.getId(), profile3.getNickname(), profile3.getPoint(), getWinRate(3, 3, 3),
                profile3.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member4.getId(), profile4.getNickname(), profile4.getPoint(), getWinRate(4, 4, 4),
                profile4.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member5.getId(), profile5.getNickname(), profile5.getPoint(), getWinRate(5, 5, 5),
                profile5.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member6.getId(), profile6.getNickname(), profile6.getPoint(), getWinRate(6, 6, 6),
                profile6.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member7.getId(), profile7.getNickname(), profile7.getPoint(), getWinRate(7, 7, 7),
                profile7.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));
        createRankingMember(member8.getId(), profile8.getNickname(), profile8.getPoint(), getWinRate(8, 8, 8),
                profile8.getRank().getRankName(), constants.getConstantsByCategory(roomInfo.getDebateCategory()));


        // redis_user_info 생성
    }

    @Test
    @Transactional
    void redisTest() {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        String pk = "pk";
        String mk = "mk";

        hashOperations.put(pk, mk, 1);
        System.out.println(hashOperations.get(pk, mk));

        Set<String> keys = redisTemplate.keys("*");
        System.out.println(keys);
        redisTemplate.delete(keys);
    }

    private RankingMember createRankingMember(Integer memberId, String nickname, int point, int winRate,
                                              RankName rankName, String redisKey){
        RankingMember rankingMember = RankingMember.builder()
                .memberId(memberId)
                .nickname(nickname)
                .winRate(winRate)
                .rankName(rankName)
                .point(point)
                .build();

        rankingMemberRedisTemplate.opsForZSet().add(Constants.RANK_REDIS_KEY, rankingMember, -rankingMember.getPoint());
        rankingMemberRedisTemplate.opsForZSet().add(redisKey, rankingMember, -rankingMember.getPoint());

        return rankingMember;
    }

    private int getWinRate(int win, int lose, int tie) {
        return win+lose==0
                ? 0
                : (int) ((double) win / (win + lose + tie) * 100.0);
    }
}