package com.a708.drwa.game.service;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.data.dto.request.AddRecordRequestDto;
import com.a708.drwa.game.data.dto.response.AddRecordResponseDto;
import com.a708.drwa.game.data.dto.response.AddRecordRedisResponseDto;
import com.a708.drwa.game.data.dto.response.WinnerTeam;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.exception.GameErrorCode;
import com.a708.drwa.game.exception.GameException;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordBulkRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private static final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    private static final int mvpPoint = 60;
    private static final int winnerPoint = 100;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public AddRecordResponseDto addRecord(AddRecordRequestDto addRecordRequestDto) {
        // Redis 통신
        final AddRecordRedisResponseDto addRecordRedisResponseDto
                = getRedisGameResponseDto(addRecordRequestDto.getDebateId());

        // 올바른 정보를 받았느지 확인
        final Debate debate = getDebate(addRecordRequestDto.getDebateId());
        final GameInfo gameInfo = getGameInfo(addRecordRequestDto.getGameId());
        
        // MySql에 저장할 전적 정보 생성
        final List<Record> records = getInputRecords();
        
        // MySQL에 전적 Bulk 저장
        recordBulkRepository.saveAll(records);

        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddRecordResponseDto.builder()
                .teamAVoteNum(addRecordRedisResponseDto.getTeamAVoteNum())
                .teamBVoteNum(addRecordRedisResponseDto.getTeamBVoteNum())
                .noVoteNum(addRecordRedisResponseDto.getJurorList().size() + addRecordRedisResponseDto.getViewerList().size()
                        - addRecordRedisResponseDto.getTeamAVoteNum() - addRecordRedisResponseDto.getTeamBVoteNum())
                .mvpMemberIds(getMvpMemberIds(addRecordRedisResponseDto.getMvpMap()))
                .mvpPoint(mvpPoint)
                .winnerPoint(winnerPoint)
                .winnerTeam(getWinnerTeam(addRecordRedisResponseDto.getTeamAVoteNum(), addRecordRedisResponseDto.getTeamBVoteNum()))
                .build();
    }

    // 클라이언트가 전달한 debateId가 존재하는지 확인
    private Debate getDebate(int debateId) {
        return debateRepository.findById(debateId)
                .orElseThrow(() -> new GameException(GameErrorCode.GAME_NOT_FOUND));
    }

    // 클라이언트가 전달한 gameId가 존재하는지 확인
    private GameInfo getGameInfo(int gameId) {
        return gameInfoRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GameErrorCode.DEBATE_NOT_FOUND));
    }

    // 레디스 통신
    private AddRecordRedisResponseDto getRedisGameResponseDto(int debateId) {
        final List<Integer> teamAList = redisUtil.getIntegerListData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_A_LIST));
        final List<Integer> teamBList = redisUtil.getIntegerListData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_B_LIST));
        final List<Integer> jurorList = redisUtil.getIntegerListData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_LIST));
        final List<Integer> viewerList = redisUtil.getIntegerListData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VIEWER_LIST));
        final Map<Integer, Integer> mvpMap = redisUtil.getIntegerIntegerMapData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.MVP));
        final int teamAVoteNum = redisUtil.getIntegerData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VOTE_TEAM_A));
        final int teamBVoteNum = redisUtil.getIntegerData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VOTE_TEAM_B));
        final String keyword = redisUtil.getData(
                redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD));

        return AddRecordRedisResponseDto.builder()
                .teamAList(teamAList)
                .teamBList(teamBList)
                .jurorList(jurorList)
                .viewerList(viewerList)
                .mvpMap(mvpMap)
                .teamAVoteNum(teamAVoteNum)
                .teamBVoteNum(teamBVoteNum)
                .keyword(keyword)
                .build();
    }

    // === 편의 메서드 ===
    // 저장할 전적
    private List<Record> getInputRecords() {
        List<Record> records = null;

        return records;
    }

    // MVP 도출
    private List<Integer> getMvpMemberIds(Map<Integer, Integer> mvpMap) {
        Map<Integer, Integer> voteResultMap = new HashMap<>();

        // MVP 에서
        mvpMap.forEach((k, v) -> {
            voteResultMap.put(v, voteResultMap.getOrDefault(v, 0) + 1);
        });

        return null;
    }

    // 승리 팀 도출
    private WinnerTeam getWinnerTeam(int teamA, int teamB) {
        if(teamA > teamB)       return WinnerTeam.A;
        else if(teamA < teamB)  return WinnerTeam.B;
        else                    return WinnerTeam.TIE;
    }
}
