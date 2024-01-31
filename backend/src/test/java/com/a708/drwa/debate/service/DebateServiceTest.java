package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.DebateMemberDto;
import com.a708.drwa.debate.data.dto.RoomInfo;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.domain.enums.Role;
import com.a708.drwa.debate.repository.DebateCategoryRepository;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import com.a708.drwa.room.domain.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    RedisUtil redisUtil;
    @Autowired
    RedisKeyUtil redisKeyUtil;
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
//        redisTemplate.delete(debateId + "");
//        redisTemplate.opsForHash().put(debateId + "", "member1", member1);
//        redisTemplate.opsForHash().put(debateId + "", "member2", member2);
//        redisTemplate.opsForHash().put(debateId + "", "member3", member3);
//
//        Member res1 = (Member) redisTemplate.opsForHash().get(debateId + "", "member1");
//        Member res2 = (Member) redisTemplate.opsForHash().get(debateId + "", "member2");
//        Member res3 = (Member) redisTemplate.opsForHash().get(debateId + "", "member3");
//
//        // then
//        assertThat(member1.getId()).isEqualTo(res1.getId());
//        assertThat(member1.getUserId()).isEqualTo(res1.getUserId());
//
//        assertThat(member2.getId()).isEqualTo(res2.getId());
//        assertThat(member2.getUserId()).isEqualTo(res2.getUserId());
//
//        assertThat(member3.getId()).isEqualTo(res3.getId());
//        assertThat(member3.getUserId()).isEqualTo(res3.getUserId());
    }

    @Test
    void start() {
        //// given
        DebateStartRequestDto testDto = create();

        String debateKey = testDto.getDebateId() + "";
        String startTimeKey = DebateRedisKey.START_TIME.string();
        String roomInfoKey = DebateRedisKey.ROOM_INFO.string();

        // phase 초기화
        redisUtil.hSet(debateKey, redisKeyUtil.getKeyByRoomIdWithKeyword(DebateRedisKey.PHASE.string()), 0);

        //// when

        // 시작 시간 및 설정 저장
        redisUtil.hSet(debateKey, startTimeKey, System.currentTimeMillis() / 1000L + "");
        redisUtil.hSet(debateKey, roomInfoKey, testDto.getRoomInfo());

        // 유저 리스트 저장
        for(Map.Entry<Integer, DebateMemberDto> member : testDto.getMemberDtoHashMap().entrySet()) {
            DebateMemberDto memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            redisUtil.setList(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        // 준비 단계로 진행
        redisTemplate.opsForHash().increment(debateKey, DebateRedisKey.PHASE, 1);

        // then
        // roomInfo check
        RoomInfo redisRoomInfo = (RoomInfo) redisUtil.hGet(debateKey, DebateRedisKey.ROOM_INFO.string());

        assertThat(redisRoomInfo.getDebateTitle()).isEqualTo(testDto.getRoomInfo().getDebateTitle());
        assertThat(redisRoomInfo.getHostId()).isEqualTo(testDto.getRoomInfo().getHostId());
        assertThat(redisRoomInfo.getPassword()).isEqualTo(testDto.getRoomInfo().getPassword());

        // UserList check
        List<Object> oLeftList = redisUtil.getList(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, Role.A_TEAM.string()));
        List<Object> oRightList = redisUtil.getList(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, Role.B_TEAM.string()));

        assertThat(((DebateMemberDto)oLeftList.get(0)).getRole()).isEqualTo(Role.A_TEAM);
        assertThat(((DebateMemberDto)oLeftList.get(1)).getRole()).isEqualTo(Role.A_TEAM);

        assertThat(((DebateMemberDto)oRightList.get(0)).getRole()).isEqualTo(Role.B_TEAM);
        assertThat(((DebateMemberDto)oRightList.get(1)).getRole()).isEqualTo(Role.B_TEAM);
    }

    private DebateStartRequestDto create() {
        HashMap<Integer, DebateMemberDto> memberMap = new HashMap<>();
        memberMap.put(2, DebateMemberDto.builder()
                .memberId(2)
                .role(Role.A_TEAM)
                .build());
        memberMap.put(3, DebateMemberDto.builder()
                .memberId(3)
                .role(Role.A_TEAM)
                .build());
        memberMap.put(4, DebateMemberDto.builder()
                .memberId(4)
                .role(Role.B_TEAM)
                .build());
        memberMap.put(5, DebateMemberDto.builder()
                .memberId(5)
                .role(Role.B_TEAM)
                .build());

        return DebateStartRequestDto.builder()
                .debateId(1)
                .roomInfo(RoomInfo.builder()
                        .debateCategoryId(1)
                        .hostId(1)
                        .debateTitle("t")
                        .leftKeyword("치킨")
                        .rightKeyword("피자")
                        .playerNum(4)
                        .jurorNum(2)
                        .isPrivate(true)
                        .password("1234")
                        .speakingTime(1)
                        .readyTime(1)
                        .qnaTime(1)
                        .thumbnail1("a")
                        .thumbnail2("b")
                        .build())
                .memberDtoHashMap(memberMap)
                .build();
    }
}