package com.a708.drwa.game.service;

import com.a708.drwa.achievement.service.AchievementService;
import com.a708.drwa.debate.data.RoomInfo;
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
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.exception.RedisErrorCode;
import com.a708.drwa.redis.exception.RedisException;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
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
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final MemberRepository memberRepository;

    // 공개 토론방 종료 시 정산 서비스
    @Transactional
    public AddGameResponseDto addGame(AddGameRequestDto addGameRequestDto) {
        // 인자로 받은 토론 방 번호가 존재하는지 확인
        checkDebateId(addGameRequestDto.getDebateId());

        // 사설방인지, 공개방인지 판단
        HashOperations<String, DebateRedisKey, Object> hash = objectRedisTemplate.opsForHash();
        RoomInfo roomInfo = (RoomInfo) hash.get(
                addGameRequestDto.getDebateId()+"",
                DebateRedisKey.ROOM_INFO);
        if(roomInfo==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

        if(roomInfo.getIsPrivate())
            return getAddPrivateGame(addGameRequestDto.getDebateId(), roomInfo);
        return getAddPublicGame(addGameRequestDto.getDebateId(), roomInfo);
    }

    private AddGameResponseDto getAddPublicGame(int debateId, RoomInfo roomInfo) {
        // Redis에서 필요한 데이터 가져옴
        final PublicGameRedisResponseDto publicGameRedisResponseDto
                = getPublicGameRedisResponseDto();

        // DB에 게임 정보, 전적을 저장하기 위해 필요한 데이터 도출
        final List<Integer> mvpList = getMvpList(publicGameRedisResponseDto.getMvpMap());
        final int mvpMemberId = mvpList.isEmpty() ? -1 : mvpList.get(0);
        final WinnerTeam winnerTeam = getWinnerTeam(
                publicGameRedisResponseDto.getTeamAVoteNum(),
                publicGameRedisResponseDto.getTeamBVoteNum());
        final List<Integer> winnerTeamList = getWinnerTeamList(
                publicGameRedisResponseDto.getTeamAList(),
                publicGameRedisResponseDto.getTeamBList(),
                winnerTeam);
        final int mvpPoint = getMvpPoint(mvpList);

        /*
         * DB에 게임 정보 저장
         * 일단 임시로 MVP로 선정된 인원이 많으면 mvpMemberId에 '-1'을 넣도록 함.
         * 추후 명확하게 설정할 필요가 있음
         */
        final GameInfo savedGameInfo = addGameInfo(
                publicGameRedisResponseDto.getKeywordA(),
                publicGameRedisResponseDto.getKeywordB(),
                mvpMemberId,
                isPrivate);

        // DB에 전적 BULK 저장
        final List<Record> records = getInputRecords(
                publicGameRedisResponseDto.getTeamAList(),
                publicGameRedisResponseDto.getTeamBList(),
                savedGameInfo,
                winnerTeam);
        recordBulkRepository.saveAll(records);

//        // === Redis 랭킹 멤버 데이터 수정 ===
//        // Redis에서 Ranking Member Set 받아온다.
//        Set<RankingMember> members = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);
//        if(members==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
//
//        // RANK_REDIS_KEY 삭제
//        rankMemberRedisTemplate.delete(RANK_REDIS_KEY);
//
//        // Set 내에서 winnerTeamList에 속한 멤버의 랭킹 정보를 필터링한다.
//        // 속하지 않은 멤버는 새로운 랭킹 멤버 리스트에 저장한다.
//        Set<RankingMember> rankingMember = members.stream()
//                .filter(m -> {
//                    // 승리팀, MVP가 아닌 멤버는 nRaningMember에 저장
//                    if(!winnerTeamList.contains(m.getMemberId()) && !mvpList.contains(m.getMemberId())) {
//                        rankMemberRedisTemplate.opsForZSet().add(RANK_REDIS_KEY, m, -m.getPoint());
//                        return false;
//                    }
//
//                    // 승리팀 혹은 MVP인 멤버는 point를 수정하여 rankingMember에 저장
//                    if(winnerTeamList.contains(m.getMemberId()))
//                        m.setPoint(m.getPoint() + MVP_POINT);
//                    if(mvpList.contains(m.getMemberId()))
//                        m.setPoint(m.getPoint() + mvpPoint);
//                    return true;
//                }).collect(Collectors.toSet());
//        if(!rankingMember.containsAll(winnerTeamList) && !rankingMember.containsAll(mvpList))
//            throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
//
//        // 포인트가 변경되는 RankMbmer를 Redis에 저장
//        for(RankingMember member : rankingMember)
//            rankMemberRedisTemplate.opsForZSet().add(RANK_REDIS_KEY, member, -member.getPoint());
//
//        // 포인트 및 랭킹 최신화
//        for(int memberId : publicGameRedisResponseDto.getTeamAList())
//            achievementService.check(memberId);
//        for(int memberId : publicGameRedisResponseDto.getTeamBList())
//            achievementService.check(memberId);

        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddGameResponseDto.builder()
                .teamAVoteNum(publicGameRedisResponseDto.getTeamAVoteNum())
                .teamBVoteNum(publicGameRedisResponseDto.getTeamBVoteNum())
                .noVoteNum(
                        publicGameRedisResponseDto.getJurorList().size()
                                + publicGameRedisResponseDto.getViewerList().size()
                                - publicGameRedisResponseDto.getTeamAVoteNum()
                                - publicGameRedisResponseDto.getTeamBVoteNum())
                .mvpList(mvpList)
                .mvpPoint(mvpPoint)
                .winnerPoint(WINNER_POINT)
                .winnerTeam(winnerTeam.name())
                .winnerTeamList(winnerTeamList)
                .isPrivate(isPrivate)
                .build();
    }

    private AddGameResponseDto getAddPrivateGame(int debateId, RoomInfo roomInfo) {
        // Redis에서 필요한 데이터 가져옴
        PrivateGameRedisResponseDto redisResponseDto
                = getPrivateGameRedisResponseDto()

        // 필요한 데이터 정의
        final WinnerTeam winnerTeam = WinnerTeam.TIE;

        /*
         * DB에 게임 정보 저장
         * 일단 임시로 MVP로 선정된 인원이 많으면 mvpMemberId에 '-1'을 넣도록 함.
         * 추후 명확하게 설정할 필요가 있음
         */
        final GameInfo savedGameInfo = addGameInfo(
                privateGameRedisResponseDto.getKeywordA(),
                privateGameRedisResponseDto.getKeywordB(),
                -1,
                isPrivate);

        // DB에 전적 BULK 저장
        final List<Record> records = getInputRecords(
                privateGameRedisResponseDto.getTeamAList(),
                privateGameRedisResponseDto.getTeamBList(),
                savedGameInfo,
                winnerTeam);
        recordBulkRepository.saveAll(records);

        // TODO : Redis 데이터 수정
//        final RankingMember rankingMember = rankingMemberRedisTemplate.opsForValue().


        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddGameResponseDto.builder()
                .isPrivate(false)
                .build();
    }

    // debateId 검증
    private void checkDebateId(int debateId) {
        if(!debateRepository.existsByDebateId(debateId))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
    }

    // 게임 정보 저장 및 검증
    @Transactional
    public GameInfo addGameInfo(String keywordA, String keywordB, int mvpMemberId, boolean isPrivate) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(keywordA + "_" + keywordB)
                .mvpMemberId(mvpMemberId)
                .isPrivate(isPrivate)
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
        List<Integer> teamAList = getRedisIntegerList(debateId, DebateRedisKey.TEAM_A.string());
        List<Integer> teamBList = getRedisIntegerList(debateId, DebateRedisKey.TEAM_B.string());
        List<Integer> jurorList = getRedisIntegerList(debateId, DebateRedisKey.JUROR.string());
        List<Integer> viewerList = getRedisIntegerList(debateId, DebateRedisKey.VIEWER.string());
        Map<Object, Object> mvpMap = getRedisMap(debateId, DebateRedisKey.MVP.string());
        int teamAVoteNum = getRedisIntData(debateId, DebateRedisKey.VOTE_TEAM_A.string());
        int teamBVoteNum = getRedisIntData(debateId, DebateRedisKey.VOTE_TEAM_B.string());
        String keywordA = getRedisStringData(debateId, DebateRedisKey.KEYWORD_A.string());
        String keywordB = getRedisStringData(debateId, DebateRedisKey.KEYWORD_B.string());

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

    // Redis 내에서 사설방 정산에 필요한 데이터 얻기
    private PrivateGameRedisResponseDto getPrivateGameRedisResponseDto(int debateId) {
        List<Integer> teamAList = getRedisIntegerList(debateId, DebateRedisKey.TEAM_A.string());
        List<Integer> teamBList = getRedisIntegerList(debateId, DebateRedisKey.TEAM_B.string());
        String keywordA = getRedisStringData(debateId, DebateRedisKey.KEYWORD_A.string());
        String keywordB = getRedisStringData(debateId, DebateRedisKey.KEYWORD_B.string());

        return PrivateGameRedisResponseDto.builder()
                .teamAList(teamAList)
                .teamBList(teamBList)
                .keywordA(keywordA)
                .keywordB(keywordB)
                .build();
    }

    // Redis에서 Integer 데이터 반환하는 메서드
    private int getRedisIntData(int debateId, String redisKey) {
        Integer result = integerRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", redisKey));
        if(result==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    // Redis에서 String 데이터 반환하는 메서드
    private String getRedisStringData(int debateId, String redisKey) {
        String result = stringRedisTemplate.opsForValue().get(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", redisKey));
        if(result==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    // Redis에서 List<Integer> 데이터 반환하는 메서드
    private List<Integer> getRedisIntegerList(int debateId, String redisKey) {
        List<Integer> result = integerRedisTemplate.opsForList().range(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", redisKey),
                0, -1);
        if(result==null) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);
        return result;
    }

    // Redis에서 Map 데이터 반환하는 메서드
    private Map<Object, Object> getRedisMap(int debateId, String redisKey) {
        return objectRedisTemplate.opsForHash().entries(
                redisKeyUtil.getKeyByRoomIdWithKeyword(debateId+"", redisKey));
    }


    // === 편의 메서드 ===
    // 저장할 전적 반환
    private List<Record> getInputRecords(List<Integer> teamAList, List<Integer> teamBList, GameInfo gameInfo, WinnerTeam winnerTeam) {
        List<Record> records = new ArrayList<>();
        List<Integer> players;
        List<Member> savedPlayers;
        Result resultA;
        Result resultB;

        // DB에서 멤버 검색
        players = List.copyOf(teamAList);
        players.addAll(teamBList);
        savedPlayers = memberRepository.findAllByIdIn(players)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

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

    // 승리 팀 리스트 도출
    private List<Integer> getWinnerTeamList(List<Integer> teamAList, List<Integer> teamBList, WinnerTeam winnerTeam) {
        if(winnerTeam == WinnerTeam.A)      return teamAList;
        else if(winnerTeam == WinnerTeam.B) return teamBList;
        else {
            List<Integer> mergedList = List.copyOf(teamAList);
            mergedList.addAll(teamBList);
            return mergedList;
        }
    }
}
