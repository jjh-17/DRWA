package com.a708.drwa.game.service;

import com.a708.drwa.game.data.dto.response.GameCreateResponseDto;
import com.a708.drwa.game.data.dto.response.WinnerTeam;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.redis.domain.DebateRedisKey;
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
public class GameService {

    private final GameInfoRepository gameInfoRepository;
    private final RecordRepository recordRepository;
    private final RedisUtil redisUtil;
    private final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();

    // MySql에 게임 정보 저장 서비스
    @Transactional
    public void addGameInfo(GameInfo gameInfo) {

    }

    // MySql에 전적 저장 서비스
    @Transactional
    public void addRecord(List<Record> records) {
        //
    }

    // Redis에

    // 전적 정보 저장 서비스

    // 게임 정보의 mvpMemberId 수정 서비스


    @Transactional
    public GameCreateResponseDto createGame(int debateId) {
        // 필요한 데이터 정의
        final RedisGameResponseDto redisGameResponseDto;
        final int mvpPoint = 50;
        final int winnerPoint = 30;
        int noVoteNum;
        int mvpMemberId;
        int gameId;
        WinnerTeam winnerTeam;  // 승리 팀

        // Redis 통신
        redisGameResponseDto = getRedisGameResponseDto(debateId);

        // 데이터 연산
        winnerTeam = getWinnerTeam(redisGameResponseDto.getTeamAVoteNum(), redisGameResponseDto.getTeamBVoteNum());
        noVoteNum = redisGameResponseDto.getJurorList().size() + redisGameResponseDto.getViewerList().size()
                - redisGameResponseDto.getTeamAVoteNum() - redisGameResponseDto.getTeamBVoteNum();
        mvpMemberId = getMVP(redisGameResponseDto.getTeamAList(), redisGameResponseDto.getTeamBList());

        // MySql에 게임 정보 저장
        gameId = gameInfoRepository.save(GameInfo.builder()
                        .keyword(redisGameResponseDto.getKeyword())
                        .mvpMemberId(mvpMemberId)
                .build()).getGameId();

        // MySql에 전적 저장
        List<Record> records = getInputRecords();
        List<Record> savedRecords = recordRepository.saveAll(records);

        return GameCreateResponseDto.builder()
                .teamAVoteNum(redisGameResponseDto.getTeamAVoteNum())
                .teamBVoteNum(redisGameResponseDto.getTeamBVoteNum())
                .noVoteNum(noVoteNum)
                .mvpMemberId(mvpMemberId)
                .mvpPoint(mvpPoint)
                .winnerPoint(winnerPoint)
                .winnerTeam(winnerTeam)
                .build();
    }

    // 레디스와 통신하여 필요한 데이터 받아옴
    private RedisGameResponseDto getRedisGameResponseDto(int debateId) {
        return RedisGameResponseDto.builder()
                .build();
    }

    // === 편의 메서드 ===
    private List<Record> getInputRecords() {
        List<Record> records = new ArrayList<>();

        return records;
    }

    // 승리 팀 반환
    private WinnerTeam getWinnerTeam(int voteTeamA, int voteTeamB) {
        if(voteTeamA > voteTeamB)       return WinnerTeam.A;
        else if(voteTeamA < voteTeamB)  return WinnerTeam.B;
        else                            return WinnerTeam.TIE;
    }

    // MVP 반환
    private int getMVP(List<Object> teamAList, List<Object> teamBList) {
        int mvpMemberId = -1;
        boolean isSame = true;
        int maxVote = 0;

        for(Iterator<Object> iter = teamAList.iterator(); iter.hasNext();) {
            int memberId = (int) iter.next();

        }

        return mvpMemberId;
    }
}
