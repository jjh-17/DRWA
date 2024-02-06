package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.VoteInfo;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.awt.color.ProfileDataException;
import java.util.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private static final int MVP_POINT = 60;
    private static final int WINNER_POINT = 100;
    private static final Random random = new Random();
    private static final Constants constants = new Constants();
    private final RedisTemplate<String, Object> objectRedisTemplate;
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private final RecordBulkRepository recordBulkRepository;
    private final GameInfoRepository gameInfoRepository;
    private final DebateRepository debateRepository;
    private final ProfileRepository profileRepository;

    // 공개 토론방 종료 시 정산 서비스
    @Transactional
    public AddGameResponseDto addGame(AddGameRequestDto addGameRequestDto) {
        // 인자로 받은 방의 카테고리 확인
        checkDebateCategory(addGameRequestDto.getDebateId());

        // redisDebate가 있는지 판단
        HashOperations<String, DebateRedisKey, Object> debateHashOperations = objectRedisTemplate.opsForHash();
        Map<DebateRedisKey, Object> redisDebate = debateHashOperations.entries(addGameRequestDto.getDebateId() + "");
        if(redisDebate.isEmpty()) throw new RedisException(RedisErrorCode.REDIS_READ_FAIL);

        // 변수 선언
        RoomInfo roomInfo = (RoomInfo) redisDebate.get(DebateRedisKey.ROOM_INFO);
        DebateMembers debateMembers = (DebateMembers) redisDebate.get(DebateRedisKey.DEBATE_MEMBER_LIST);
        VoteInfo voteInfo = (VoteInfo) redisDebate.get(DebateRedisKey.VOTE_INFO);
        Map<String, Integer> mvpMap = (HashMap<String, Integer>) redisDebate.get(DebateRedisKey.MVP);
        Map<String, String> voteMap = (HashMap<String, String>) redisDebate.get(DebateRedisKey.VOTE_MAP);

        // DB에 게임 정보, 전적을 저장하기 위해 필요한 데이터 도출
        List<Profile> profiles = findProfiles(debateMembers.getLeftMembers(), debateMembers.getRightMembers(), debateMembers.getJurors());
        List<DebateMember> mvpList = getMvpList(mvpMap, debateMembers.getLeftMembers(), debateMembers.getRightMembers());
        int mvpMemberId = mvpList.isEmpty()
                ? -1
                : mvpList.get(random.nextInt(mvpList.size()-1)).getMemberId();
        WinnerTeam winnerTeam = getWinnerTeam(voteInfo);
        List<DebateMember> winnerTeamList = getWinnerTeamList(
                debateMembers.getLeftMembers(),
                debateMembers.getRightMembers(),
                winnerTeam);
        int mvpPoint = getMvpPoint(mvpList);
        int winnerPoint = winnerTeamList.size()==debateMembers.getLeftMembers().size()+debateMembers.getRightMembers().size()
                ? WINNER_POINT / 2
                : WINNER_POINT;

        // Db에 게임정보, 전적 저장
        GameInfo savedGameInfo = updateDB(roomInfo, debateMembers, profiles, voteMap, mvpMemberId, winnerTeam);

        // Map을 순회하며 Redis 통신
        String redisCategory = constants.getConstantsByCategory(roomInfo.getDebateCategory());
        updateRedis(profiles, winnerTeamList, mvpList, savedGameInfo, winnerTeam, redisCategory,
                winnerPoint, mvpPoint, mvpMemberId, savedGameInfo.isPrivate());

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

    // 게임 정보, 전적 저장
    public GameInfo updateDB(RoomInfo roomInfo, DebateMembers debateMembers, List<Profile> profiles,
                          Map<String, String> voteMap, int mvpMemberId, WinnerTeam winnerTeam) {
        final GameInfo savedGameInfo = addGameInfo(
                roomInfo.getLeftKeyword(),
                roomInfo.getRightKeyword(),
                mvpMemberId,
                roomInfo.getIsPrivate());

        // DB에 전적 BULK 저장
        List<Record> records = new ArrayList<>();
        for(Profile profile : profiles) {
            Member member = profile.getMember();
            DebateMember debateMember = checkIdInDebateMembers(debateMembers.getLeftMembers(), member.getId());
            if(debateMember==null) checkIdInDebateMembers(debateMembers.getRightMembers(), member.getId());
            if(debateMember==null) checkIdInDebateMembers(debateMembers.getJurors(), member.getId());
            if(debateMember==null) throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);

            records.add(Record.builder()
                    .member(profile.getMember())
                    .gameInfo(savedGameInfo)
                    .result(getResult(debateMember, winnerTeam, voteMap))
                    .team(Team.valueOf(debateMember.getRole().string())).build());
        }
        recordBulkRepository.saveAll(records);

        return savedGameInfo;
    }

    // Redis 최신화 메서드
    public void updateRedis(List<Profile> profiles, List<DebateMember> winnerTeamList, List<DebateMember> mvpList,
                             GameInfo gameInfo, WinnerTeam winnerTeam, String redisCategory,
                             int winnerPoint, int mvpPoint, int mvpMemberId, boolean isPrivate) {
        HashOperations<String, MemberRedisKey, Object> memberRedisOperations = objectRedisTemplate.opsForHash();
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

        for(Profile profile : profiles) {
            Member member = profile.getMember();
            String redisUserInfoRankKey = member.getUserId() + ":" + Constants.RANK_REDIS_KEY;
            String redisUserInfoCategoryKey = member.getUserId() + ":" + redisCategory;

            // member의 userId를 이용하여 'redis_user_info' 검색
            Map<MemberRedisKey, Object> redisRankUserInfo = memberRedisOperations.entries(redisUserInfoRankKey);
            Map<MemberRedisKey, Object> redisCategoryUserInfo = memberRedisOperations.entries(redisUserInfoCategoryKey);

            RankingMember[] rankRankingMembers = updateRedisUserInfo(profile, redisRankUserInfo, addPointMap, roleMap,
                    gameInfo, winnerTeam, redisUserInfoRankKey, mvpMemberId);
            RankingMember[] categoryRankingMembers = updateRedisUserInfo(profile, redisCategoryUserInfo, addPointMap, roleMap,
                    gameInfo, winnerTeam, redisUserInfoCategoryKey, mvpMemberId);

            if(!isPrivate) {
                updateRedisRanking(rankRankingMembers[0], rankRankingMembers[1], Constants.RANK_REDIS_KEY, redisUserInfoRankKey);
                updateRedisRanking(categoryRankingMembers[0], categoryRankingMembers[1], redisCategory, redisUserInfoCategoryKey);
            }
        }
    }

    // 'redis_user_info' 업데이트
    private RankingMember[] updateRedisUserInfo(Profile profile, Map<MemberRedisKey, Object> redisUserInfo,
                                                Map<Integer, Integer> addPointMap, Map<Integer, Role> roleMap,
                                                GameInfo gameInfo, WinnerTeam winnerTeam, String key, int mvpMemberId) {
        RankingMember[] rankingMembers = new RankingMember[2];

        // 'redis_user_info'내 데이터를 이용하여 'redis_ranking' 내
        List<Record> records = (List<Record>) redisUserInfo.get(MemberRedisKey.LATEST_GAME_RECORD);
        String rankName = (String) redisUserInfo.get(MemberRedisKey.RANK_NAME);
        String selectedAchievement = (String) redisUserInfo.get(MemberRedisKey.SELECTED_ACHIEVEMENT);
        int point = (int) redisUserInfo.get(MemberRedisKey.POINT);
        int winCount = (int) redisUserInfo.get(MemberRedisKey.WIN_COUNT);
        int loseCount = (int) redisUserInfo.get(MemberRedisKey.LOSE_COUNT);
        int tieCount = (int) redisUserInfo.get(MemberRedisKey.TIE_COUNT);
        int mvpCount = (int) redisUserInfo.get(MemberRedisKey.MVP_COUNT);
        int winRate = winCount+loseCount==0
                ? 0
                : (int) ((double) winCount / (winCount + loseCount + tieCount) * 100.0);
        Member member = profile.getMember();

        // 랭킹 멤버 생성
        rankingMembers[0] = RankingMember.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                .winRate(winRate)
                .rankName(RankName.valueOf(rankName))
                .achievement(selectedAchievement)
                .build();

        // === 'redis_user_info' 최신화 ===
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = objectRedisTemplate.opsForHash();

        // 포인트 최신화
        point += addPointMap.getOrDefault(member.getId(), 0);
        memberHashOperations.put(key, MemberRedisKey.POINT, point);

        // 승, 패, 무, 전적 저장 및 최신화
        Record record;
        if(winnerTeam == WinnerTeam.TIE) {
            memberHashOperations.put(key, MemberRedisKey.TIE_COUNT, ++tieCount);
            record = Record.builder()
                    .gameInfo(gameInfo)
                    .member(member)
                    .result(Result.TIE)
                    .build();
        }
        else if(winnerTeam.string().equals(roleMap.get(member.getId()).string())){
            memberHashOperations.put(key, MemberRedisKey.WIN_COUNT, ++winCount);
            record = Record.builder()
                    .gameInfo(gameInfo)
                    .member(member)
                    .result(Result.WIN)
                    .build();
        }
        else {
            memberHashOperations.put(key, MemberRedisKey.LOSE_COUNT, ++loseCount);
            record = Record.builder()
                    .gameInfo(gameInfo)
                    .member(member)
                    .result(Result.LOSE)
                    .build();
        }
        records.add(record);
        memberHashOperations.put(key, MemberRedisKey.LATEST_GAME_RECORD, records);

        // MVP 횟수 최신화
        if(mvpMemberId == member.getId())
            memberHashOperations.put(key, MemberRedisKey.MVP_COUNT, ++mvpCount);

        // 승률 연산
        winRate = winCount+loseCount==0
                ? 0
                : (int) ((double) winCount / (winCount + loseCount + tieCount) * 100.0);

        rankingMembers[1] = RankingMember.builder()
                .memberId(member.getId())
                .nickname(profile.getNickname())
                .winRate(winRate)
                .rankName(RankName.valueOf(rankName))
                .achievement(selectedAchievement)
                .build();

        // 칭호, 대표 칭호, 티어는 어떻게 업데이트 해야할지 잘 모르겠음

        return rankingMembers;
    }

    // redis_ranking 최신화
    // redis_user_info의 raking 또한 최신화
    public void updateRedisRanking(RankingMember rankingMember, RankingMember nRankingMember, String redisRankingKey, String redisUserInfoKey) {
        ZSetOperations<String, RankingMember> rankingMemberZSetOperations = rankMemberRedisTemplate.opsForZSet();
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = objectRedisTemplate.opsForHash();

        Long cnt = rankingMemberZSetOperations.remove(redisRankingKey, rankingMember);
        if(cnt==null || cnt!=1) throw new RedisException(RedisErrorCode.REDIS_DELETE_FAIL);

        rankingMemberZSetOperations.add(redisRankingKey, nRankingMember, -nRankingMember.getPoint());

        // redis_user_info의 rakning 최신화
        Long ranking = rankingMemberZSetOperations.rank(redisUserInfoKey, nRankingMember);
        if(ranking==null) throw new RedisException(RedisErrorCode.REDIS_UPDATE_FAIL);

        memberHashOperations.put(redisUserInfoKey, MemberRedisKey.RANKING, ranking);
    }

    // debateId 검증
    private void checkDebateCategory(int debateId) {
        if(!debateRepository.existsById(debateId))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
    }

    // DB내 프로필 검색
    private List<Profile> findProfiles(List<DebateMember> teamAList, List<DebateMember> teamBList, List<DebateMember> jurorList) {
        List<Integer> ids = new ArrayList<>();

        for(DebateMember teamA : teamAList) ids.add(teamA.getMemberId());
        for(DebateMember teamB : teamBList) ids.add(teamB.getMemberId());
        for(DebateMember juror : jurorList) ids.add(juror.getMemberId());

        List<Profile> profiles = profileRepository.findAllByMemberIdIn(ids)
                .orElseThrow(() -> new ProfileDataException(ProfileErrorCode.PROFILE_NOT_FOUND.getErrorCode()));
        if(profiles.size() != teamAList.size()+teamBList.size()+jurorList.size())
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
    private Result getResult(DebateMember debateMember, WinnerTeam winnerTeam, Map<String, String> voteMap) {
        // 비김
        if(winnerTeam == WinnerTeam.TIE)
            return Result.TIE;

        // 배심원
        if(debateMember.getRole() == Role.JUROR) {
            if(winnerTeam.string().equals(voteMap.get(debateMember.getMemberId()+"")))
                return Result.WIN;
            return Result.LOSE;
        }

        // Left, Right
        return debateMember.getRole().string().equals(winnerTeam.string())
                ? Result.WIN
                : Result.LOSE;
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

    DebateMember checkIdInDebateMembers(List<DebateMember> debateMembers, int memberId) {
        for(DebateMember debateMember : debateMembers) {
            if(debateMember.getMemberId() == memberId) return debateMember;
        }
        return null;
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
