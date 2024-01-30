package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Result;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@SpringBootTest
@Transactional
class RecordBulkRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired GameInfoRepository gameInfoRepository;
    @Autowired RecordBulkRepository recordBulkRepository;
    @Autowired RecordRepository recordRepository;

    @Test
    void saveAll() {

        // 멤버 저장
        Member member1 = Member.builder()
                .socialType(SocialType.NAVER)
                .userId("test1")
                .build();
        Member member2 = Member.builder()
                .socialType(SocialType.GOOGLE)
                .userId("test2")
                .build();
        Member member3 = Member.builder()
                .socialType(SocialType.KAKAO)
                .userId("test3")
                .build();
        memberRepository.saveAll(List.of(member1, member2, member3));

        // 게임 정보 저장
        GameInfo gameInfo1 = GameInfo.builder()
                .keyword("keyword1")
                .build();
        gameInfoRepository.save(gameInfo1);

        // 전적 저장
        Record record1 = Record.builder()
                .member(member1)
                .gameInfo(gameInfo1)
                .team(Team.A)
                .result(Result.WIN)
                .build();
        Record record2 = Record.builder()
                .member(member2)
                .gameInfo(gameInfo1)
                .team(Team.B)
                .result(Result.LOSE)
                .build();
        Record record3 = Record.builder()
                .member(member3)
                .gameInfo(gameInfo1)
                .team(Team.B)
                .result(Result.LOSE)
                .build();

//        recordBulkRepository.saveAll(List.of(record1));
//        recordBulkRepository.saveAll(List.of(record1, record2));
        recordBulkRepository.saveAll(List.of(record1, record2, record3));

//        System.out.println(recordRepository.findAll());

//        recordRepository.deleteAll(List.of(record1, record2));
    }
}