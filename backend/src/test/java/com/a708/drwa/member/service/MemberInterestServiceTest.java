package com.a708.drwa.member.service;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.domain.MemberInterest;
import com.a708.drwa.member.repository.MemberInterestRepository;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Profile("test")
@Transactional
class MemberInterestServiceTest {
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberInterestRepository memberInterestRepository;

    @Test
    @DisplayName("관심사 추가")
    @Order(1)
    void updateMemberInterests() {
        // given
        Member member = addMember();
        addInterest(member);

        // when
        List<MemberInterest> memberInterests = member.getMemberInterestList();

        // then
        assertEquals(memberInterests.size(), 3);
    }

    @Test
    @DisplayName("관심사 삭제")
    @Order(2)
    void deleteTest() {
        // given
        Member member = addMember();
        addInterest(member);

        // when
        int size = member.getMemberInterestList().size();
        member.removeAllInterest();

        // then
        assertEquals(size, 3);
        assertEquals(member.getMemberInterestList().size(), 0);
    }

    public Member addMember() {
        return memberRepository.save(Member.builder()
                .socialType(SocialType.GOOGLE)
                .userId("test")
                .build());
    }

    public void addInterest(Member member) {
        MemberInterest.builder()
                .member(member)
                .debateCategory(DebateCategory.CULTURE)
                .build();
        MemberInterest.builder()
                .member(member)
                .debateCategory(DebateCategory.ANIMAL)
                .build();
        MemberInterest.builder()
                .member(member)
                .debateCategory(DebateCategory.ECONOMY)
                .build();
    }

    @AfterEach
    public void deleteMember() {
        memberRepository.deleteByUserId("test");
    }
}