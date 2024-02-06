package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.repository.RankRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GameServiceTest {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired DebateRepository debateRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired RankRepository rankRepository;
    @Autowired GameService gameService;
    RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    final String debateKey = "1";


    @BeforeEach
    void before() {
        // Debate 생성 1
        Debate debate = Debate.builder().debateCategory(DebateCategory.ANIMAL).build();
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
                .nickname("nickname1").member(member2).point(2).mvpCount(2).rank(rank).build();
        Profile profile3 = Profile.builder()
                .nickname("nickname1").member(member3).point(3).mvpCount(3).rank(rank).build();
        Profile profile4 = Profile.builder()
                .nickname("nickname1").member(member4).point(4).mvpCount(4).rank(rank).build();
        Profile profile5 = Profile.builder()
                .nickname("nickname1").member(member5).point(5).mvpCount(5).rank(rank).build();
        Profile profile6 = Profile.builder()
                .nickname("nickname1").member(member6).point(6).mvpCount(6).rank(rank).build();
        Profile profile7 = Profile.builder()
                .nickname("nickname1").member(member7).point(7).mvpCount(7).rank(rank).build();
        Profile profile8 = Profile.builder()
                .nickname("nickname1").member(member8).point(8).mvpCount(8).rank(rank).build();
        profileRepository.saveAll(List.of(profile1, profile2, profile3, profile4, profile5, profile6, profile7, profile8));

        // DebateMembers 저장

       HashOperations<String, DebateRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        Map<Integer, DebateMember> memberDtoHashMap = new HashMap<>();
        memberDtoHashMap.put(1, DebateMember.builder().memberId(1).nickName("1").role(Role.A_TEAM).build());
        memberDtoHashMap.put(2, DebateMember.builder().memberId(2).nickName("2").role(Role.B_TEAM).build());
        memberDtoHashMap.put(3, DebateMember.builder().memberId(3).nickName("3").role(Role.JUROR).build());
        memberDtoHashMap.put(4, DebateMember.builder().memberId(4).nickName("4").role(Role.WATCHER).build());
        for(Map.Entry<Integer, DebateMember> member : memberDtoHashMap.entrySet()) {
            DebateMember memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        Map<String, Integer> mvpMap = new HashMap<>();
        mvpMap.put("1", 4);
        mvpMap.put("2", 4);
        mvpMap.put("3", 4);
        hashOperations.put(debateKey, DebateRedisKey.MVP, mvpMap);

        hashOperations.put(debateKey, DebateRedisKey.KEYWORD_A, "test");
    }

    @Test
    void redisTest() {
        HashOperations<String, DebateRedisKey, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        // 방 정보 가져오기
        RoomInfo roomInfo = (RoomInfo) hashOperations.get(debateKey, DebateRedisKey.ROOM_INFO);

        // 참여자 리스트
        DebateMembers debateMembers = (DebateMembers) hashOperations.get(debateKey, DebateRedisKey.DEBATE_MEMBER_LIST);

        // MVP
        Map<String, Integer> mvpMap = (HashMap<String, Integer>) hashOperations.get(debateKey, DebateRedisKey.MVP);
        System.out.println(hashOperations.get(debateKey, DebateRedisKey.MVP.toString()));

        System.out.println(hashOperations.get(debateKey, DebateRedisKey.KEYWORD_A.toString()));

    }
}