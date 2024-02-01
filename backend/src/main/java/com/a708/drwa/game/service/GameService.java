package com.a708.drwa.game.service;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.data.dto.request.AddGameRequestDto;
import com.a708.drwa.game.data.dto.response.*;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Result;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.game.exception.GameInfoErrorCode;
import com.a708.drwa.game.exception.GameInfoException;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordBulkRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.member.exception.MemberException;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private static final RedisKeyUtil redisKeyUtil = new RedisKeyUtil();
    private static final int MVP_POINT = 60;
    private static final int WINNER_POINT = 100;
    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<Object, Object> objectRedisTemplate;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final MemberRepository memberRepository;

    // 공개 토론방 종료 시 정산 서비스
    @Transactional
    public AddPublicGameResponseDto addPublicGame(AddGameRequestDto addGameRequestDto) {
        // Redis에서 필요한 데이터 가져옴
        final PublicGameRedisResponseDto addRecordRedisResponseDto
                = getPublicGameRedisResponseDto(addGameRequestDto.getDebateId());

        // DB에서 인자로 받은 토론방 Id 검색
        Debate savedDebate = getDebate(addGameRequestDto.getDebateId());

        // DB에 게임 정보, 전적을 저장하기 위해 필요한 데이터 도출
        final List<Integer> mvpList = getMvpList(addRecordRedisResponseDto.getMvpMap());
        final int mvpMemberId = mvpList.size()==1 ? mvpList.get(0) : -1;
        final WinnerTeam winnerTeam = getWinnerTeam(
                addRecordRedisResponseDto.getTeamAVoteNum(),
                addRecordRedisResponseDto.getTeamBVoteNum());
        final int mvpPoint = getMvpPoint(mvpList);

        /*
         * DB에 게임 정보 저장
         * 일단 임시로 MVP로 선정된 인원이 많으면 mvpMemberId에 '-1'을 넣도록 함.
         * 추후 명확하게 설정할 필요가 있음
         */
        final GameInfo savedGameInfo = addGameInfo(
                addRecordRedisResponseDto.getKeywordA(),
                addRecordRedisResponseDto.getKeywordB(),
                mvpMemberId);

        // DB에 전적 BULK 저장
        final List<Record> records = getInputRecords(
                addRecordRedisResponseDto.getTeamAList(),
                addRecordRedisResponseDto.getTeamBList(),
                savedGameInfo,
                winnerTeam);
        recordBulkRepository.saveAll(records);

        // TODO : Redis 데이터 수정


        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddPublicGameResponseDto.builder()
                .teamAVoteNum(addRecordRedisResponseDto.getTeamAVoteNum())
                .teamBVoteNum(addRecordRedisResponseDto.getTeamBVoteNum())
                .noVoteNum(
                        addRecordRedisResponseDto.getJurorList().size()
                                + addRecordRedisResponseDto.getViewerList().size()
                                - addRecordRedisResponseDto.getTeamAVoteNum()
                                - addRecordRedisResponseDto.getTeamBVoteNum())
                .mvpList(mvpList)
                .mvpPoint(mvpPoint)
                .winnerPoint(WINNER_POINT)
                .winnerTeam(winnerTeam.name())
                .build();
    }

    // 토론 방 검색
    private Debate getDebate(int debateId) {
        return debateRepository.findById(debateId)
                .orElseThrow(() -> new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR));
    }

    // 게임 정보 저장 및 검증
    @Transactional
    public GameInfo addGameInfo(String keywordA, String keywordB, int mvpMemberId) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(keywordA + "_" + keywordB)
                .mvpMemberId(mvpMemberId)
                .build();

        GameInfo savedGameInfo = gameInfoRepository.save(gameInfo);
        if(!gameInfo.getKeyword().equals(savedGameInfo.getKeyword())
                || gameInfo.getMvpMemberId()!= savedGameInfo.getMvpMemberId())
            throw new GameInfoException(GameInfoErrorCode.GAME_INFO_MISMATCH);

        return savedGameInfo;
    }

    // === Redis ===
    // Redis 내에서 공개방 정산에 필요한 데이터 얻기
    private PublicGameRedisResponseDto getPublicGameRedisResponseDto(int debateId) {
        List<Integer> teamAList;
        List<Integer> teamBList;
        List<Integer> jurorList;
        List<Integer> viewerList;
        Map<Object, Object> mvpMap;
        int teamAVoteNum;
        int teamBVoteNum;
        String keywordA;
        String keywordB;

        teamAList = integerRedisTemplate.opsForList().range(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.TEAM_A.string()),
                0, -1);
        teamBList = integerRedisTemplate.opsForList().range(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.TEAM_B.string()),
                0, -1);
        jurorList = integerRedisTemplate.opsForList().range(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.JUROR.string()),
                0, -1);
        viewerList = integerRedisTemplate.opsForList().range(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.VIEWER.string()),
                0, -1);
        mvpMap = objectRedisTemplate.opsForHash().entries(redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+""));
        teamAVoteNum = Integer.parseInt(String.valueOf(integerRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.VOTE_TEAM_A.string()))));
        teamBVoteNum = Integer.parseInt(String.valueOf(integerRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.VOTE_TEAM_B.string()))));
        keywordA = stringRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.KEYWORD_A.string()));
        keywordB = stringRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", DebateRedisKey.KEYWORD_B.string()));

        return PublicGameRedisResponseDto.builder()
                .teamAList(teamAList)
                .teamBList(teamBList)
                .jurorList(jurorList)
                .viewerList(viewerList)
                .mvpMap(mvpMap)
                .teamAVoteNum(teamAVoteNum)
                .teamBVoteNum(teamBVoteNum)
                .keywordA(keywordA)
                .keywordB(keywordB)
                .build();
    }

    // === 편의 메서드 ===
    // 저장할 전적 반환
    private List<Record> getInputRecords(List<Integer> teamAList, List<Integer> teamBList, GameInfo gameInfo, WinnerTeam winnerTeam) {
        List<Record> records = new ArrayList<>();
        List<Integer> players;
        List<Member> savedPlayers;
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
        players = List.copyOf(teamAList);
        players.addAll(teamBList);
        savedPlayers = memberRepository.findAllByIdIn(players)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 레코드 추가
        for(Member player : savedPlayers) {
            records.add(Record.builder()
                    .member(player)
                    .gameInfo(gameInfo)
                    .result(teamAList.contains(player.getId()) ? resultA : resultB)
                    .team(teamAList.contains(player.getId()) ? Team.A : Team.B).build());
        }

        return records;
    }

    // MVP 도출
    public List<Integer> getMvpList(Map<Object, Object> mvpMap) {
        Map<Integer, Integer> voteResultMap = new HashMap<>();
        List<Integer> mvpList = new ArrayList<>();

        // MVP로 선정된 인원이 아무도 없다면 빈 리스트 반환
        if(mvpMap.keySet().isEmpty()) return mvpList;

        // mvpMap을 순회하며 투표 결과 합산
        mvpMap.forEach((k, v) -> voteResultMap.put((int) v, voteResultMap.getOrDefault((int) v, 0) + 1));

        // mvpMap의 key 값을 value 내림차순으로 정렬
        List<Integer> keys = new ArrayList<>(voteResultMap.keySet());
        keys.sort((o1, o2) -> voteResultMap.get(o2) - voteResultMap.get(o1));

        // keys를 순회하며 mvpList에 저장
        int maxVoteNum = voteResultMap.get(keys.get(0));

        // MVP 리스트
        if(maxVoteNum > 0) {
            for(int key : keys) {
                if(voteResultMap.get(key) < maxVoteNum) break;
                mvpList.add(key);
            }
        }

        return mvpList;
    }

    // MVP 포인트 도출
    private int getMvpPoint(List<Integer> mvpList) {
        int size = mvpList.size();
        return size==0 ? 0 : GameService.MVP_POINT /size;
    }

    // 승리 팀 도출
    private WinnerTeam getWinnerTeam(int teamA, int teamB) {
        if(teamA > teamB)       return WinnerTeam.A;
        else if(teamA < teamB)  return WinnerTeam.B;
        else                    return WinnerTeam.TIE;
    }
}
