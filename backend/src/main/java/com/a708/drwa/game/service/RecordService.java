package com.a708.drwa.game.service;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.data.dto.request.AddRecordRequestDto;
import com.a708.drwa.game.data.dto.response.AddRecordResponseDto;
import com.a708.drwa.game.data.dto.response.AddRecordRedisResponseDto;
import com.a708.drwa.game.data.dto.response.WinnerTeam;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Result;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.game.exception.GameErrorCode;
import com.a708.drwa.game.exception.GameException;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordBulkRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecordService {

    private static final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    private static final int MVP_POINT = 60;
    private static final int WINNER_POINT = 100;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public AddRecordResponseDto addRecord(AddRecordRequestDto addRecordRequestDto) {
        // Redis에서 필요한 데이터 가져옴
        final AddRecordRedisResponseDto addRecordRedisResponseDto = getRedisGameResponseDto(addRecordRequestDto.getDebateId());

        // 올바른 정보를 받았는지 검증
        final Debate debate = checkDebateId(addRecordRequestDto.getDebateId());
        final GameInfo gameInfo = checkGameId(addRecordRequestDto.getGameId());

        // MySql에 저장할 전적 정보 생성
        final WinnerTeam winnerTeam = getWinnerTeam(addRecordRedisResponseDto.getTeamAVoteNum(), addRecordRedisResponseDto.getTeamBVoteNum());
        final List<Record> records = getInputRecords(
                addRecordRedisResponseDto.getTeamAList(),
                addRecordRedisResponseDto.getTeamBList(),
                gameInfo,
                winnerTeam);
        
        // MySQL에 전적 Bulk 저장
        int[] recordSaveResult = recordBulkRepository.saveAll(records);
        if(recordSaveResult==null) throw new GameException(GameErrorCode.BATCH_UPDATE_FAIL);

        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddRecordResponseDto.builder()
                .teamAVoteNum(addRecordRedisResponseDto.getTeamAVoteNum())
                .teamBVoteNum(addRecordRedisResponseDto.getTeamBVoteNum())
                .noVoteNum(addRecordRedisResponseDto.getJurorList().size() + addRecordRedisResponseDto.getViewerList().size()
                        - addRecordRedisResponseDto.getTeamAVoteNum() - addRecordRedisResponseDto.getTeamBVoteNum())
                .mvpList(getMvpList(addRecordRedisResponseDto.getMvpMap()))
                .mvpPoint(MVP_POINT)
                .winnerPoint(WINNER_POINT)
                .winnerTeam(winnerTeam.name())
                .build();
    }

    // === 검증 ===
    // 토론 방 검증
    private Debate checkDebateId(int debateId) {
        Debate debate = debateRepository.findById(debateId)
                .orElseThrow(() -> new GameException(GameErrorCode.GAME_NOT_FOUND));

        if(debateId != debate.getDebateId())
            throw new GameException(GameErrorCode.BAD_REQUEST);

        return debate;
    }
    
    // 게임 정보 검증
    private GameInfo checkGameId(int gameId) {
        GameInfo gameInfo =  gameInfoRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GameErrorCode.DEBATE_NOT_FOUND));

        if(gameId != gameInfo.getGameId())
            throw new GameException(GameErrorCode.BAD_REQUEST);

        return gameInfo;
    }

    // 플레이어 검증
    private List<Member> checkPlayers(List<Integer> teamAList, List<Integer> teamBList) {
        List<Member> players;
        List<Integer> mergedList = new ArrayList<>(teamAList);
        mergedList.addAll(teamBList);

        // DB에서 데이터 가져오기
        try {
            players = memberRepository.findAllById(mergedList);
        } catch(Exception e) {
            throw new GameException(GameErrorCode.MEMBER_NOT_FOUND);
        }

        for(Member player : players) {
            if(!mergedList.contains(player.getId()))
                throw new GameException(GameErrorCode.BAD_REQUEST);
        }

        return players;
    }


    // === Redis ===
    // Redis 내에서 정산에 필요한 데이터 얻기
    private AddRecordRedisResponseDto getRedisGameResponseDto(int debateId) {
        List<Integer> teamAList;
        List<Integer> teamBList;
        List<Integer> jurorList;
        List<Integer> viewerList;
        Map<Integer, Integer> mvpMap;
        int teamAVoteNum;
        int teamBVoteNum;
        String keywordA;
        String keywordB;

//        teamAList = redisUtil.getListData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_A_LIST));
//        teamBList = redisUtil.getListData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_B_LIST));
//        jurorList = redisUtil.getListData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_LIST));
//        viewerList = redisUtil.getListData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VIEWER_LIST));
//        mvpMap = redisUtil.getListData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.MVP));
        teamAVoteNum = redisUtil.getIntegerData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VOTE_TEAM_A));
        teamBVoteNum = redisUtil.getIntegerData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VOTE_TEAM_B));
        keywordA = redisUtil.getData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_A));
        keywordB = redisUtil.getData(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_B));
        return AddRecordRedisResponseDto.builder()
//                .teamAList(teamAList)
//                .teamBList(teamBList)
//                .jurorList(jurorList)
//                .viewerList(viewerList)
//                .mvpMap(mvpMap)
                .teamAVoteNum(teamAVoteNum)
                .teamBVoteNum(teamBVoteNum)
                .keywordA(keywordA)
                .keywordB(keywordB)
                .build();
    }
    
    // Redis에서 더 이상 쓰지 않는 데이터 삭제
    public void deleteRedisDebateData(int debateId) {
        List<String> keys = new ArrayList<>();
        for (DebateRedisKey key : DebateRedisKey.values()) {
            keys.add(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, key));
        }

        redisUtil.deleteDataAll(keys);
    }

    // === 편의 메서드 ===
    // 저장할 전적 반환
    private List<Record> getInputRecords(List<Integer> teamAList, List<Integer> teamBList, GameInfo gameInfo, WinnerTeam winnerTeam) {
        List<Record> records = new ArrayList<>();
        Result resultA;
        Result resultB;

        // A, B 팀의 결과 도출
        if(winnerTeam == WinnerTeam.A) {
            resultA = Result.WIN;
            resultB = Result.LOSE;
        } else if (winnerTeam == WinnerTeam.B) {
            resultA = Result.LOSE;
            resultB = Result.WIN;
        } else{
            resultA = Result.TIE;
            resultB = Result.TIE;
        }

        // DB에서 멤버 검색
        List<Member> players = checkPlayers(teamAList, teamBList);

        // 레코드 추가
        for(Member player: players) {
            records.add(Record.builder()
                            .member(player)
                            .gameInfo(gameInfo)
                            .result(teamAList.contains(player.getId()) ? resultA : resultB)
                            .team(teamAList.contains(player.getId()) ? Team.A : Team.B).build());
        }

        return records;
    }

    // MVP 도출
    public List<Integer> getMvpList(Map<Integer, Integer> mvpMap) {
        Map<Integer, Integer> voteResultMap = new HashMap<>();
        List<Integer> mvpList = new ArrayList<>();

        // MVP로 선정된 인원이 아무도 없다면 빈 리스트 반환
        if(mvpMap.keySet().isEmpty()) return mvpList;

        // mvpMap을 순회하며 투표 결과 합산
        mvpMap.forEach((k, v) -> voteResultMap.put(v, voteResultMap.getOrDefault(v, 0) + 1));

        // mvpMap의 key 값을 value 내림차순으로 정렬
        List<Integer> keys = new ArrayList<>(voteResultMap.keySet());
        keys.sort((o1, o2) -> voteResultMap.get(o2) - voteResultMap.get(o1));

        // keys를 순회하며 mvpList에 저장
        int maxVoteNum = voteResultMap.get(keys.get(0));
        for(int key : keys) {
            if(voteResultMap.get(key) < maxVoteNum) break;
            mvpList.add(key);
        }

        return mvpList;
    }

    // 승리 팀 도출
    private WinnerTeam getWinnerTeam(int teamA, int teamB) {
        if(teamA > teamB)       return WinnerTeam.A;
        else if(teamA < teamB)  return WinnerTeam.B;
        else                    return WinnerTeam.TIE;
    }
}
