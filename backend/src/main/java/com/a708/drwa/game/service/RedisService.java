package com.a708.drwa.game.service;

import com.a708.drwa.game.data.dto.response.AddRecordResponseDto;
import com.a708.drwa.game.data.dto.response.AddRecordRedisResponseDto;
import com.a708.drwa.game.data.dto.response.WinnerTeam;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.repository.RecordRepository;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RecordRepository recordRepository;
    private final RedisUtil redisUtil;
    private static final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    private static final int mvpPoint = 30;
    private static final int winnerPoint = 100;
    private AddRecordRedisResponseDto addRecordRedisResponseDto;

    @Transactional
    public AddRecordResponseDto addRecord(int debateId) {
        // Redis 통신
        addRecordRedisResponseDto = getRedisGameResponseDto(debateId);

        // MySql에 전적 저장
        List<Record> records = getInputRecords();
        List<Record> savedRecords = recordRepository.saveAll(records);

        return AddRecordResponseDto.builder()
                .teamAVoteNum(addRecordRedisResponseDto.getTeamAVoteNum())
                .teamBVoteNum(addRecordRedisResponseDto.getTeamBVoteNum())
                .noVoteNum(getNoVoteNum())
                .mvpMemberId(getMvpMemberId())
                .mvpPoint(mvpPoint)
                .winnerPoint(winnerPoint)
                .winnerTeam(getWinnerTeam())
                .build();
    }

    // 레디스 통신
    private AddRecordRedisResponseDto getRedisGameResponseDto(int debateId) {
        return AddRecordRedisResponseDto.builder()
                .build();
    }

    // === 편의 메서드 ===
    // 미투표 수 도출 메서드
    private int getNoVoteNum() {
        return addRecordRedisResponseDto.getJurorList().size() + addRecordRedisResponseDto.getViewerList().size()
                - addRecordRedisResponseDto.getTeamAVoteNum() - addRecordRedisResponseDto.getTeamBVoteNum();
    }

    // 승리 팀 도출
    private WinnerTeam getWinnerTeam() {
        if(addRecordRedisResponseDto.getTeamAVoteNum() > addRecordRedisResponseDto.getTeamBVoteNum())       return WinnerTeam.A;
        else if(addRecordRedisResponseDto.getTeamAVoteNum() < addRecordRedisResponseDto.getTeamBVoteNum())  return WinnerTeam.B;
        else                                                                                                return WinnerTeam.TIE;
    }

    // MVP 도출
    private int getMvpMemberId() {
        int mvpMemberId = -1;
        boolean isSame = true;
        int maxVote = 0;

        // MVP 에서

        return mvpMemberId;
    }

    private static List<Record> getInputRecords() {
        List<Record> records = null;

        return records;
    }
}
