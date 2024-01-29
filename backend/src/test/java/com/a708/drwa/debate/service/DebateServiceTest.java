package com.a708.drwa.debate.service;

import com.a708.drwa.debate.repository.DebateCategoryRepository;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.context.annotation.Profile("test")
@Transactional
class DebateServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    DebateRepository debateRepository;
    @Autowired
    DebateCategoryRepository debateCategoryRepository;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void init() {
        Member member1 = Member.builder()
                .userId("test1")
                .socialType(SocialType.GOOGLE)
                .build();

        Member member2 = Member.builder()
                .userId("test2")
                .socialType(SocialType.GOOGLE)
                .build();

        Member member3 = Member.builder()
                .userId("test3")
                .socialType(SocialType.GOOGLE)
                .build();
        Member member4 = Member.builder()
                .userId("test4")
                .socialType(SocialType.GOOGLE)
                .build();

        Member member5 = Member.builder()
                .userId("test5")
                .socialType(SocialType.GOOGLE)
                .build();

        Member member6 = Member.builder()
                .userId("test6")
                .socialType(SocialType.GOOGLE)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
    }

    @Test
    void addList() {
        // given
        int debateId = 1;

        Member member1 = memberRepository.findByUserId("test1")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member member2 = memberRepository.findByUserId("test2")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member member3 = memberRepository.findByUserId("test3")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));

        // when
        redisTemplate.delete(debateId + "");
        redisTemplate.opsForHash().put(debateId + "", "member1", member1);
        redisTemplate.opsForHash().put(debateId + "", "member2", member2);
        redisTemplate.opsForHash().put(debateId + "", "member3", member3);

        Member res1 = (Member) redisTemplate.opsForHash().get(debateId + "", "member1");
        Member res2 = (Member) redisTemplate.opsForHash().get(debateId + "", "member2");
        Member res3 = (Member) redisTemplate.opsForHash().get(debateId + "", "member3");

        // then
        assertThat(member1.getId()).isEqualTo(res1.getId());
        assertThat(member1.getUserId()).isEqualTo(res1.getUserId());

        assertThat(member2.getId()).isEqualTo(res2.getId());
        assertThat(member2.getUserId()).isEqualTo(res2.getUserId());

        assertThat(member3.getId()).isEqualTo(res3.getId());
        assertThat(member3.getUserId()).isEqualTo(res3.getUserId());
    }

    @Test
    void start() {
        // given
        int debateId = 1;
        String key = debateId + "";

        // A팀 참여자
        Member aMember1 = memberRepository.findByUserId("a1")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member aMember2 = memberRepository.findByUserId("a2")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member aMember3 = memberRepository.findByUserId("a3")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));

        // B팀 참여자
        Member bMember1 = memberRepository.findByUserId("b1")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member bMember2 = memberRepository.findByUserId("b2")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
        Member bMember3 = memberRepository.findByUserId("b3")
                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));

        // 배심원
//        Member cMember1 = memberRepository.findByUserId("c1")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
//        Member cMember2 = memberRepository.findByUserId("c2")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
//        Member cMember3 = memberRepository.findByUserId("c3")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
//
//        // 관전자
//        Member dMember1 = memberRepository.findByUserId("d1")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
//        Member dMember2 = memberRepository.findByUserId("d2")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));
//        Member dMember3 = memberRepository.findByUserId("d3")
//                .orElseThrow(() -> new MemberException(MemberErrorCode.EXAMPLE_ERROR_CODE));

        ////// 팀 유저 리스트 저장
        String AKey = DebateRedisKey.TEAM_A_LIST.string();
        String BKey = DebateRedisKey.TEAM_B_LIST.string();
        String CKey = DebateRedisKey.JUROR_LIST.string();
        String DKey = DebateRedisKey.VIEWER_LIST.string();

        // 팀 A
        redisTemplate.opsForList().rightPush(AKey, aMember1);
        redisTemplate.opsForList().rightPush(AKey, aMember2);
        redisTemplate.opsForList().rightPush(AKey, aMember3);

        // 팀 B
        redisTemplate.opsForList().rightPush(BKey, bMember1);
        redisTemplate.opsForList().rightPush(BKey, bMember2);
        redisTemplate.opsForList().rightPush(BKey, bMember3);

//        // 배심원
//        redisTemplate.opsForList().rightPush(CKey, cMember1);
//        redisTemplate.opsForList().rightPush(CKey, cMember2);
//        redisTemplate.opsForList().rightPush(CKey, cMember3);
//
//        // 관전자
//        redisTemplate.opsForList().rightPush(DKey, dMember1);
//        redisTemplate.opsForList().rightPush(DKey, dMember2);
//        redisTemplate.opsForList().rightPush(DKey, dMember3);

        // 대기 단계 PHASE => 0
        redisTemplate.opsForHash().put(key, DebateRedisKey.PHASE, 0);

        // when
        redisTemplate.opsForHash().put(key, DebateRedisKey.START_TIME, System.currentTimeMillis() / 1000L);
        redisTemplate.opsForHash().increment(key, DebateRedisKey.PHASE, 1);
        redisTemplate.opsForHash().put(key, DebateRedisKey.LEFT_KEYWORD, "치킨");
        redisTemplate.opsForHash().put(key, DebateRedisKey.RIGHT_KEYWORD, "피자");

        // A팀 리스트
        List<Object> oLeftList = redisTemplate.opsForList().range(AKey, 0, -1);
        List<Member> LeftList = new ArrayList<>();
        for(Object o : oLeftList)
            LeftList.add((Member) o);

        // B팀 리스트
        List<Object> oRightList = redisTemplate.opsForList().range(BKey, 0, -1);
        List<Member> RightList = new ArrayList<>();
        for(Object o : oRightList)
            RightList.add((Member) o);

        // then
        // roomInfo check
        assertThat("치킨").isEqualTo(redisTemplate.opsForHash().get(key, DebateRedisKey.LEFT_KEYWORD));
        assertThat("피자").isEqualTo(redisTemplate.opsForHash().get(key, DebateRedisKey.RIGHT_KEYWORD));
        assertThat(1).isEqualTo(redisTemplate.opsForHash().get(key, DebateRedisKey.PHASE));

        // UserList check
        assertThat(aMember1.getUserId()).isEqualTo(LeftList.get(0).getUserId());
        assertThat(aMember2.getUserId()).isEqualTo(LeftList.get(1).getUserId());
        assertThat(aMember3.getUserId()).isEqualTo(LeftList.get(2).getUserId());

        assertThat(bMember1.getUserId()).isEqualTo(RightList.get(0).getUserId());
        assertThat(bMember2.getUserId()).isEqualTo(RightList.get(1).getUserId());
        assertThat(bMember3.getUserId()).isEqualTo(RightList.get(2).getUserId());
    }
}