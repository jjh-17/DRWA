package com.a708.drwa.profile.service;

import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.dto.request.AddProfileRequest;
import com.a708.drwa.profile.dto.response.ProfileResponse;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.redis.RankMember;
import com.a708.drwa.rank.service.RankService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.context.annotation.Profile("test")
@Transactional
class ProfileServiceTest {
    @Autowired
    ProfileRepository profileRepository;

    @Autowired ProfileService profileService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RankService rankService;
    @Autowired
    RedisTemplate <String, RankMember> rankMemberRedisTemplate;

    @BeforeEach
    public void initRanks(){
        rankService.initAllRanks();
        rankMemberRedisTemplate.delete("rank");
    }

    @Test
    void addProfile() {
        // given
        Member member = Member.builder()
                .build();
        Member savedMember = memberRepository.save(member);

        AddProfileRequest request = AddProfileRequest.builder()
                .nickname("testNickname")
                .memberId(savedMember.getId())
                .build();

        // when
        profileService.addProfile(request);
        Profile profile = profileRepository.findByMemberId(savedMember.getId()).get();

        // then
        assertThat(profile.getNickname()).isEqualTo("testNickname");
        assertThat(profile.getId()).isEqualTo(savedMember.getId());
        assertThat(profile.getRank().getRankName()).isEqualTo(RankName.BRONZE);
        assertThat(profile.getPoint()).isEqualTo(1000);
    }

    @Test
    void rankMemberRedisSaveTest(){
        Member member1 = Member.builder()
                .build();
        Member member2 = Member.builder()
                .build();
        List<Member> members = memberRepository.saveAll(List.of(member1, member2));

        AddProfileRequest request1 = AddProfileRequest.builder()
                .nickname("testNickname1")
                .memberId(members.get(0).getId())
                .build();
        AddProfileRequest request2 = AddProfileRequest.builder()
                .nickname("testNickname2")
                .memberId(members.get(1).getId())
                .build();
        profileService.addProfile(request1);
        profileService.addProfile(request2);

        // when
        Set<RankMember> ranks = rankMemberRedisTemplate.opsForZSet().range("rank", 0, -1);
        for (RankMember rank : ranks) {
            System.out.println(rank.getMemberId() + " " + rank.getPoint());
        }

        // then
        assertThat(ranks.size()).isEqualTo(2);
        assertThat(ranks)
                .extracting("point")
                .contains(1000);
    }

    @Test
    void updateProfile() {
    }

    @Test
    void findProfileByMemberId() {
        // given
        Member member = Member.builder()
                .build();
        Member savedMember = memberRepository.save(member);

        AddProfileRequest request = AddProfileRequest.builder()
                .nickname("testNickname")
                .memberId(savedMember.getId())
                .build();
        profileService.addProfile(request);

        // when
        ProfileResponse profile = profileService.findProfileByMemberId(savedMember.getId());

        // then
        assertThat(profile.getMemberId()).isEqualTo(savedMember.getId());
        assertThat(profile.getMvpCount()).isEqualTo(0);
        assertThat(profile.getNickname()).isEqualTo("testNickname");
        assertThat(profile.getPoint()).isEqualTo(1000);

    }

    @Test
    void rankMemberRedisFindById(){
        // given
        Member member = Member.builder()
                .build();
        Member savedMember = memberRepository.save(member);

        AddProfileRequest request = AddProfileRequest.builder()
                .nickname("testNickname")
                .memberId(savedMember.getId())
                .build();
        profileService.addProfile(request);

        // when
        ProfileResponse profile = profileService.findProfileByMemberId(savedMember.getId());

        // then
        assertThat(profile.getMemberId()).isEqualTo(savedMember.getId());
        assertThat(profile.getMvpCount()).isEqualTo(0);
        assertThat(profile.getNickname()).isEqualTo("testNickname");
        assertThat(profile.getPoint()).isEqualTo(1000);
    }

    @Test
    void findAll() {
        // given
        Member member1 = Member.builder()
                .build();
        Member member2 = Member.builder()
                .build();
        Member member3 = Member.builder()
                .build();
        Member member4 = Member.builder()
                .build();
        List<Member> savedMembers = memberRepository.saveAll(List.of(member1, member2, member3, member4));

        AddProfileRequest request1 = AddProfileRequest.builder()
                .nickname("1")
                .memberId(savedMembers.get(0).getId())
                .build();
        AddProfileRequest request2 = AddProfileRequest.builder()
                .nickname("2")
                .memberId(savedMembers.get(1).getId())
                .build();
        AddProfileRequest request3 = AddProfileRequest.builder()
                .nickname("3")
                .memberId(savedMembers.get(2).getId())
                .build();
        AddProfileRequest request4 = AddProfileRequest.builder()
                .nickname("4")
                .memberId(savedMembers.get(3).getId())
                .build();

        profileService.addProfile(request1);
        profileService.addProfile(request2);
        profileService.addProfile(request3);
        profileService.addProfile(request4);

        // when
        List<ProfileResponse> results = profileService.findAllWithDto();

        // then
        assertThat(results.size()).isEqualTo(4);
        assertThat(results)
                .extracting("nickname")
                .contains("1", "2", "3", "4");
        assertThat(results)
                .extracting("point")
                .containsExactly(1000, 1000, 1000, 1000);

    }
}