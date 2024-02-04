package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.VoteInfo;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.exception.RoomInfoErrorCode;
import com.a708.drwa.debate.exception.RoomInfoException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.data.dto.request.AddGameRequestDto;
import com.a708.drwa.game.data.dto.response.AddGameResponseDto;
import com.a708.drwa.game.data.dto.response.WinnerTeam;
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
    private final RedisTemplate<String, Integer> integerRedisTemplate;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final MemberRepository memberRepository;
    private static final Random random = new Random();

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

        if(roomInfo.getIsPrivate()==null)
            throw new RoomInfoException(RoomInfoErrorCode.ROOM_INFO_OMISSION_ERROR);

        if(Boolean.TRUE.equals(roomInfo.getIsPrivate()))
            return getAddPrivateGame(addGameRequestDto.getDebateId(), roomInfo);
        return getAddPublicGame(addGameRequestDto.getDebateId(), roomInfo);
    }

    private AddGameResponseDto getAddPublicGame(int debateId, RoomInfo roomInfo) {
        // 변수 선언
        HashOperations<String, DebateRedisKey, Object> hash = objectRedisTemplate.opsForHash();
        String debateKey = debateId+"";

        // 투표 정보, 토론방 내 유저 리스트, MVP 투표 맵 가져오기
        DebateMembers debateMembers = (DebateMembers) hash.get(debateKey, DebateRedisKey.DEBATE_MEMBER_LIST);
        VoteInfo voteInfo = (VoteInfo) hash.get(debateKey, DebateRedisKey.VOTE_INFO);
        Map<String, Integer> mvpMap = (HashMap<String, Integer>) hash.get(debateKey, DebateRedisKey.MVP);
        if(debateMembers==null || voteInfo==null || mvpMap==null)
            throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

        // DB에 게임 정보, 전적을 저장하기 위해 필요한 데이터 도출
        final List<DebateMember> mvpList = getMvpList(mvpMap, debateMembers.getLeftMembers(), debateMembers.getRightMembers());
        final int mvpMemberId = mvpList.isEmpty()
                ? -1
                : mvpList.get(random.nextInt(mvpList.size()-1)).getMemberId();
        final WinnerTeam winnerTeam = getWinnerTeam(voteInfo);
        final List<DebateMember> winnerTeamList = getWinnerTeamList(
                debateMembers.getLeftMembers(),
                debateMembers.getRightMembers(),
                winnerTeam);
        final int mvpPoint = getMvpPoint(mvpList);

        /*
         * DB에 게임 정보 저장
         * 일단 임시로 MVP로 선정된 인원이 많으면 mvpMemberId에 '-1'을 넣도록 함.
         */
        final GameInfo savedGameInfo = addGameInfo(
                roomInfo.getLeftKeyword(),
                roomInfo.getRightKeyword(),
                mvpMemberId,
                roomInfo.getIsPrivate());

        // DB에 전적 BULK 저장
        final List<Record> records = getInputRecords(
                debateMembers.getLeftMembers(),
                debateMembers.getRightMembers(),
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
                .teamAVoteNum(voteInfo.getLeftVote())
                .teamBVoteNum(voteInfo.getRightVote())
                .noVoteNum(debateMembers.getJurors().size() + debateMembers.getWatchers().size()
                        - voteInfo.getLeftVote() - voteInfo.getRightVote())
                .mvpList(mvpList)
                .mvpPoint(mvpPoint)
                .winnerPoint(WINNER_POINT)
                .winnerTeam(winnerTeam.name())
                .winnerTeamList(winnerTeamList)
                .isPrivate(roomInfo.getIsPrivate())
                .build();
    }

    private AddGameResponseDto getAddPrivateGame(int debateId, RoomInfo roomInfo) {
        // 변수 선언
        HashOperations<String, DebateRedisKey, Object> hash = objectRedisTemplate.opsForHash();
        String debateKey = debateId+"";
        WinnerTeam winnerTeam = WinnerTeam.TIE;

        // 투표 정보, 토론방 내 유저 리스트, MVP 투표 맵 가져오기
        DebateMembers debateMembers = (DebateMembers) hash.get(debateKey, DebateRedisKey.DEBATE_MEMBER_LIST);
        if(debateMembers==null)
            throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

        /*
         * DB에 게임 정보 저장
         *
         */
        final GameInfo savedGameInfo = addGameInfo(
                roomInfo.getLeftKeyword(),
                roomInfo.getRightKeyword(),
                -1,
                roomInfo.getIsPrivate());

        // DB에 전적 BULK 저장
        final List<Record> records = getInputRecords(
                debateMembers.getLeftMembers(),
                debateMembers.getRightMembers(),
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

    // === 편의 메서드 ===
    // 저장할 전적 반환
    private List<Record> getInputRecords(List<DebateMember> teamAList, List<DebateMember> teamBList, GameInfo gameInfo, WinnerTeam winnerTeam) {
        List<Record> records = new ArrayList<>();
        List<Member> players;
        List<Integer> ids = new ArrayList<>();
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

        // DB 내에서 플레이어 검색
        for(DebateMember teamA : teamAList) ids.add(teamA.getMemberId());
        for(DebateMember teamB : teamBList) ids.add(teamB.getMemberId());
        players = memberRepository.findAllByIdIn(ids)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 레코드 추가
        for(Member player : players) {
            records.add(Record.builder()
                    .member(player)
                    .gameInfo(gameInfo)
                    .result(checkIdInDebateMembers(teamAList, player.getId())!=null ? resultA : resultB)
                    .team(checkIdInDebateMembers(teamAList, player.getId())!=null ? Team.A : Team.B).build());
        }

        return records;
    }
    private DebateMember checkIdInDebateMembers(List<DebateMember> debateMembers, int id) {
        for(DebateMember debateMember : debateMembers) {
            if(debateMember.getMemberId() == id) return debateMember;
        }
        return null;
    }

    // MVP 도출
    public List<DebateMember> getMvpList(Map<String, Integer> mvpMap, List<DebateMember> teamAList, List<DebateMember> teamBList) {
        Map<Integer, Integer> voteResultMap = new HashMap<>();
        List<DebateMember> mvpList = new ArrayList<>();

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

            DebateMember debateMember = checkIdInDebateMembers(teamAList, key);
            if(debateMember==null) debateMember = checkIdInDebateMembers(teamBList, key);
            if(debateMember==null) throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);

            mvpList.add(DebateMember.builder()
                    .memberId(debateMember.getMemberId())
                    .nickName(debateMember.getNickName())
                    .role(debateMember.getRole())
                    .build());
        }

        return mvpList;
    }

    // MVP 포인트 도출
    private int getMvpPoint(List<DebateMember> mvpList) {
        int size = mvpList.size();
        return size==0 ? 0 : GameService.MVP_POINT /size;
    }

    // 승리 팀 도출
    private WinnerTeam getWinnerTeam(VoteInfo voteInfo) {
        if(voteInfo.getLeftVote() > voteInfo.getRightVote())        return WinnerTeam.A;
        else if(voteInfo.getLeftVote() < voteInfo.getRightVote())   return WinnerTeam.B;
        else                                                        return WinnerTeam.TIE;
    }

    // 승리 팀 리스트 도출
    private List<DebateMember> getWinnerTeamList(List<DebateMember> teamAList, List<DebateMember> teamBList, WinnerTeam winnerTeam) {
        if(winnerTeam == WinnerTeam.A)      return teamAList;
        else if(winnerTeam == WinnerTeam.B) return teamBList;
        else {
            List<DebateMember> mergedList = List.copyOf(teamAList);
            mergedList.addAll(teamBList);
            return mergedList;
        }
    }
}
