package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.VoteInfo;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.Role;
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
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.exception.ProfileErrorCode;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.redis.constant.Constants;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.domain.MemberRedisKey;
import com.a708.drwa.redis.exception.RedisErrorCode;
import com.a708.drwa.redis.exception.RedisException;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.color.ProfileDataException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private static final int MVP_POINT = 60;
    private static final int WINNER_POINT = 100;
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final ProfileRepository profileRepository;
    private static final Random random = new Random();

    // 공개 토론방 종료 시 정산 서비스
    @Transactional
    public AddGameResponseDto addGame(AddGameRequestDto addGameRequestDto) {
        // 인자로 받은 토론 방 번호가 존재하는지 확인
        checkDebateId(addGameRequestDto.getDebateId());

        // 사설방인지, 공개방인지 판단
        HashOperations<String, DebateRedisKey, Object> debateHashOperations = objectRedisTemplate.opsForHash();
        Map<DebateRedisKey, Object> redisDebate = debateHashOperations.entries(addGameRequestDto.getDebateId() + "");
        if(redisDebate.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

        RoomInfo roomInfo = (RoomInfo) redisDebate.get(DebateRedisKey.ROOM_INFO);
        if(roomInfo==null)
            throw new RoomInfoException(RoomInfoErrorCode.ROOM_INFO_OMISSION_ERROR);

        if(roomInfo.getIsPrivate())
            return getAddPrivateGame(redisDebate);
        return getAddPublicGame(redisDebate);
    }

    private AddGameResponseDto getAddPublicGame(Map<DebateRedisKey, Object> redisDebate) {
        // 변수 선언
        HashOperations<String, MemberRedisKey, Object> memberRedisOperations = objectRedisTemplate.opsForHash();
        RoomInfo roomInfo = (RoomInfo) redisDebate.get(DebateRedisKey.ROOM_INFO);
        DebateMembers debateMembers = (DebateMembers) redisDebate.get(DebateRedisKey.DEBATE_MEMBER_LIST);
        VoteInfo voteInfo = (VoteInfo) redisDebate.get(DebateRedisKey.VOTE_INFO);
        Map<String, Integer> mvpMap = (HashMap<String, Integer>) redisDebate.get(DebateRedisKey.MVP);

        // DB에서 각 참가자의 멤버 리스트를 검색
        List<Profile> profiles = findProfiles(debateMembers.getLeftMembers(), debateMembers.getRightMembers());

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
        final int winnerPoint = winnerTeamList.size()==debateMembers.getLeftMembers().size()+debateMembers.getRightMembers().size()
                ? WINNER_POINT / 2
                : WINNER_POINT;

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
                profiles,
                savedGameInfo,
                winnerTeam);
        recordBulkRepository.saveAll(records);

        // 포인트 변화가 있는 멤버의 (memberId, 얻는 포인트)
        Map<Integer, Integer> addPointMap = new HashMap<>();
        Map<Integer, Role> roleMap = new HashMap<>();
        for(DebateMember winner : winnerTeamList) {
            addPointMap.put(winner.getMemberId(), addPointMap.getOrDefault(winner.getMemberId(), 0)+winnerPoint);
            roleMap.put(winner.getMemberId(), winner.getRole());
        }

        for(DebateMember mvp : mvpList) {
            addPointMap.put(mvp.getMemberId(), addPointMap.getOrDefault(mvp.getMemberId(), 0)+mvpPoint);
            roleMap.put(mvp.getMemberId(), mvp.getRole());
        }

        // Map을 순회하며 Redis 통신
        for(Profile profile : profiles) {
            // member의 userId를 이영하여 'redis_user_info' 검색
            Map<MemberRedisKey, Object> redisUserInfo = memberRedisOperations.entries(profile.getMember().getUserId());
            if(redisUserInfo.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

            updateRedisUserInfo(profile, redisUserInfo, addPointMap, roleMap, winnerTeam, mvpMemberId);
        }

        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddGameResponseDto.builder()
                .teamAVoteNum(voteInfo.getLeftVote())
                .teamBVoteNum(voteInfo.getRightVote())
                .noVoteNum(debateMembers.getJurors().size() + debateMembers.getWatchers().size()
                        - voteInfo.getLeftVote() - voteInfo.getRightVote())
                .mvpList(mvpList)
                .mvpPoint(mvpPoint)
                .winnerPoint(winnerPoint)
                .winnerTeam(winnerTeam.name())
                .winnerTeamList(winnerTeamList)
                .isPrivate(roomInfo.getIsPrivate())
                .build();
    }

    private AddGameResponseDto getAddPrivateGame(Map<DebateRedisKey, Object> entries) {
        // 변수 선언f
        WinnerTeam winnerTeam = WinnerTeam.TIE;
        RoomInfo roomInfo = (RoomInfo) entries.get(DebateRedisKey.ROOM_INFO);
        DebateMembers debateMembers = (DebateMembers) entries.get(DebateRedisKey.DEBATE_MEMBER_LIST);

        // DB에서 각 참가자의 멤버 리스트를 검색
        List<Profile> profiles = findProfiles(debateMembers.getLeftMembers(), debateMembers.getRightMembers());

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
                profiles,
                savedGameInfo,
                winnerTeam);
        recordBulkRepository.saveAll(records);

        // 전적 저장 이후 클라이언트에게 전달할 데이터
        return AddGameResponseDto.builder()
                .isPrivate(false)
                .build();
    }

    // 'redis_user_info' 업데이트
    private void updateRedisUserInfo(Profile profile, Map<MemberRedisKey, Object> redisUserInfo,
                                     Map<Integer, Integer> addPointMap, Map<Integer, Role> roleMap, WinnerTeam winnerTeam,
                                     int mvpMemberId) {
        // 'redis_user_info'내 데이터를 이용하여 'redis_ranking' 내
        int point = (int) redisUserInfo.get(MemberRedisKey.POINT);
        int winCount = (int) redisUserInfo.get(MemberRedisKey.WIN_COUNT);
        int loseCount = (int) redisUserInfo.get(MemberRedisKey.LOSE_COUNT);
        int tieCount = (int) redisUserInfo.get(MemberRedisKey.TIE_COUNT);
        int mvpCount = (int) redisUserInfo.get(MemberRedisKey.MVP_COUNT);
        String rankName = (String) redisUserInfo.get(MemberRedisKey.RANK_NAME);
        String selectedAchievement = (String) redisUserInfo.get(MemberRedisKey.SELECTED_ACHIEVEMENT);
        int winRate = winCount+loseCount==0
                ? 0
                : (int) ((double) winCount / (winCount + loseCount + tieCount) * 100.0);
        Member member = profile.getMember();

        // 랭킹 멤버 생성
        RankingMember rankingMember = RankingMember.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                .winRate(winRate)
                .rankName(RankName.valueOf(rankName))
                .achievement(selectedAchievement)
                .build();

        // === 'redis_user_info' 최신화 ===
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = objectRedisTemplate.opsForHash();

        // 포인트
        memberHashOperations.put(member.getUserId(), MemberRedisKey.POINT, point + addPointMap.getOrDefault(member.getId(), 0));

        // 승, 패, 무
        if(winnerTeam == WinnerTeam.TIE)
            memberHashOperations.put(member.getUserId(), MemberRedisKey.TIE_COUNT, tieCount + 1);
        else if(winnerTeam.string().equals(roleMap.get(member.getId()).string()))
            memberHashOperations.put(member.getUserId(), MemberRedisKey.WIN_COUNT, winCount + 1);
        else
            memberHashOperations.put(member.getUserId(), MemberRedisKey.LOSE_COUNT, loseCount+1);

        // MVP 횟수
        if(mvpMemberId == member.getId())
            memberHashOperations.put(member.getUserId(), MemberRedisKey.MVP_COUNT, mvpCount+1);

        // 전적


        updateRedisRanking(rankingMember);
    }

    private void updateRedisRanking(RankingMember rankingMember) {
        rankMemberRedisTemplate.opsForZSet().reverseRank(Constants.RANK_REDIS_KEY, rankingMember);

    }

    // debateId 검증
    private void checkDebateId(int debateId) {
        if(!debateRepository.existsByDebateId(debateId))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
    }
    
    private List<Profile> findProfiles(List<DebateMember> teamAList, List<DebateMember> teamBList) {
        List<Integer> ids = new ArrayList<>();

        for(DebateMember teamA : teamAList) ids.add(teamA.getMemberId());
        for(DebateMember teamB : teamBList) ids.add(teamB.getMemberId());

        List<Profile> profiles = profileRepository.findAllByMemberId(ids)
                .orElseThrow(() -> new ProfileDataException(ProfileErrorCode.PROFILE_NOT_FOUND.getErrorCode()));
        if(profiles.size() != teamAList.size()+teamBList.size())
            throw new ProfileDataException(ProfileErrorCode.PROFILE_NOT_FOUND.getErrorCode());

        return profiles;
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
    private List<Record> getInputRecords(List<DebateMember> teamAList, List<Profile> profiles, GameInfo gameInfo, WinnerTeam winnerTeam) {
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

        // 레코드 추가
        for(Profile profile : profiles) {
            records.add(Record.builder()
                    .member(profile.getMember())
                    .gameInfo(gameInfo)
                    .result(checkIdInDebateMembers(teamAList, profile.getMember().getId())!=null ? resultA : resultB)
                    .team(checkIdInDebateMembers(teamAList, profile.getMember().getId())!=null ? Team.A : Team.B).build());
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
        return size==0 ? 0 : GameService.MVP_POINT / size;
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
