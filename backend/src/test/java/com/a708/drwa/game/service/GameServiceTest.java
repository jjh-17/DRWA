package com.a708.drwa.game.service;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.VoteInfo;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.game.data.dto.request.AddGameRequestDto;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordRepository;
import com.a708.drwa.member.domain.Member;
import com.a708.drwa.member.repository.MemberRepository;
import com.a708.drwa.member.type.SocialType;
import com.a708.drwa.profile.domain.Profile;
import com.a708.drwa.profile.repository.ProfileRepository;
import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.repository.RankRepository;
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.redis.constant.Constants;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.domain.MemberRedisKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GameServiceTest {
    @Autowired RedisTemplate<String, Object> redisTemplate;
    @Autowired RedisTemplate<String, RankingMember> rankingMemberRedisTemplate;
    @Autowired DebateRepository debateRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ProfileRepository profileRepository;
    @Autowired RankRepository rankRepository;
    @Autowired GameInfoRepository gameInfoRepository;
    @Autowired RecordRepository recordRepository;
    @Autowired GameService gameService;

    Constants constants = new Constants();
    Debate savedDebate;
    List<Profile> savedProfiles;

    @BeforeEach
    void before() {
        redisTemplate.delete(redisTemplate.keys("*"));

        HashOperations<String, DebateRedisKey, Object> debateHashOperations = redisTemplate.opsForHash();

        // Debate 생성 1
        Debate debate = Debate.builder().debateCategory(DebateCategory.ANIMAL).build();
        savedDebate = debateRepository.save(debate);
        String debateRedisKey = savedDebate.getDebateId()+"";
        String redisCategory = constants.getConstantsByCategory(savedDebate.getDebateCategory().getValue());

        // 멤버 생성 8
        Member member1 = Member.builder().userId("userId1").socialType(SocialType.KAKAO).build();
        Member member2 = Member.builder().userId("userId2").socialType(SocialType.KAKAO).build();
        Member member3 = Member.builder().userId("userId3").socialType(SocialType.KAKAO).build();
        Member member4 = Member.builder().userId("userId4").socialType(SocialType.KAKAO).build();
        Member member5 = Member.builder().userId("userId5").socialType(SocialType.KAKAO).build();
        Member member6 = Member.builder().userId("userId6").socialType(SocialType.KAKAO).build();
        Member member7 = Member.builder().userId("userId7").socialType(SocialType.KAKAO).build();
        Member member8 = Member.builder().userId("userId8").socialType(SocialType.KAKAO).build();

        member1 = memberRepository.save(member1);
        member2 = memberRepository.save(member2);
        member3 = memberRepository.save(member3);
        member4 = memberRepository.save(member4);
        member5 = memberRepository.save(member5);
        member6 = memberRepository.save(member6);
        member7 = memberRepository.save(member7);
        member8 = memberRepository.save(member8);

        // 랭크 생성 1
        Rank rank = Rank.builder().rankName(RankName.DIAMOND).pivotPoint(1).build();
        rankRepository.save(rank);

        // 프로필 생성 8
        Profile profile1 = Profile.builder()
                .nickname("nickname1").member(member1).point(0).mvpCount(0).rank(rank).build();
        Profile profile2 = Profile.builder()
                .nickname("nickname2").member(member2).point(0).mvpCount(0).rank(rank).build();
        Profile profile3 = Profile.builder()
                .nickname("nickname3").member(member3).point(0).mvpCount(0).rank(rank).build();
        Profile profile4 = Profile.builder()
                .nickname("nickname4").member(member4).point(0).mvpCount(0).rank(rank).build();
        Profile profile5 = Profile.builder()
                .nickname("nickname5").member(member5).point(0).mvpCount(0).rank(rank).build();
        Profile profile6 = Profile.builder()
                .nickname("nickname6").member(member6).point(0).mvpCount(0).rank(rank).build();
        Profile profile7 = Profile.builder()
                .nickname("nickname7").member(member7).point(0).mvpCount(0).rank(rank).build();
        Profile profile8 = Profile.builder()
                .nickname("nickname8").member(member8).point(0).mvpCount(0).rank(rank).build();

        profile1 = profileRepository.save(profile1);
        profile2 = profileRepository.save(profile2);
        profile3 = profileRepository.save(profile3);
        profile4 = profileRepository.save(profile4);
        profile5 = profileRepository.save(profile5);
        profile6 = profileRepository.save(profile6);
        profile7 = profileRepository.save(profile7);
        profile8 = profileRepository.save(profile8);
        savedProfiles = List.of(profile1, profile2, profile3, profile4, profile5, profile6, profile7, profile8);

        // RoomInfo 생성
        RoomInfo roomInfo = RoomInfo.builder()
                .debateCategory(debate.getDebateCategory().getValue())
                .hostId(member1.getId())
                .debateTitle("privateTitle")
                .leftKeyword("privateLeft").rightKeyword("privateRight")
                .playerNum(4).jurorNum(3)
//                .isPrivate(true).password("privatePwd")
                .isPrivate(false).password(null)
                .speakingTime(100).readyTime(300).qnaTime(200)
                .build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.ROOM_INFO, roomInfo);

        // DebateMembers 생성
        DebateMember debateMember1 = DebateMember.builder()
                .memberId(member1.getId()).nickName(profile1.getNickname()).role(Role.A_TEAM).build();
        DebateMember debateMember2 = DebateMember.builder()
                .memberId(member2.getId()).nickName(profile2.getNickname()).role(Role.A_TEAM).build();
        DebateMember debateMember3 = DebateMember.builder()
                .memberId(member3.getId()).nickName(profile3.getNickname()).role(Role.B_TEAM).build();
        DebateMember debateMember4 = DebateMember.builder()
                .memberId(member4.getId()).nickName(profile4.getNickname()).role(Role.B_TEAM).build();
        DebateMember debateMember5 = DebateMember.builder()
                .memberId(member5.getId()).nickName(profile5.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember6 = DebateMember.builder()
                .memberId(member6.getId()).nickName(profile6.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember7 = DebateMember.builder()
                .memberId(member7.getId()).nickName(profile7.getNickname()).role(Role.JUROR).build();
        DebateMember debateMember8 = DebateMember.builder()
                .memberId(member8.getId()).nickName(profile8.getNickname()).role(Role.WATCHER).build();
        DebateMembers debateMembers = DebateMembers.builder()
                .leftMembers(List.of(debateMember1, debateMember2))
                .rightMembers(List.of(debateMember3, debateMember4))
                .jurors(List.of(debateMember5, debateMember6, debateMember7))
                .watchers(List.of(debateMember8))
                .build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.DEBATE_MEMBER_LIST, debateMembers);

        // VoteInfo 생성
        VoteInfo voteInfo = VoteInfo.builder().leftVote(2).rightVote(1).build();
        debateHashOperations.put(debateRedisKey, DebateRedisKey.VOTE_INFO, voteInfo);

        // MVP_MAP 생성
        Map<String, Integer> mvpMap = new HashMap<>();
        mvpMap.put(member5.getId()+"", member1.getId());
//        mvpMap.put(member6.getId()+"", member1.getId());
//        mvpMap.put(member7.getId()+"", member4.getId());
        debateHashOperations.put(debateRedisKey, DebateRedisKey.MVP, mvpMap);

        // 배심원의 VOTE_MAP 생성
        Map<String, String> voteMap = new HashMap<>();
        voteMap.put(member5.getId()+"", Team.A.string());
//        voteMap.put(member6.getId()+"", Team.B.string());
//        voteMap.put(member7.getId()+"", Team.B.string());
        debateHashOperations.put(debateRedisKey, DebateRedisKey.VOTE_MAP, voteMap);

        // redis_ranking 생성
        long ranking1[] = createRankingMember(member1.getId(), profile1.getNickname(), profile1.getPoint(), getWinRate(0, 0, 0),
                profile1.getRank().getRankName(), redisCategory);
        long ranking2[] = createRankingMember(member2.getId(), profile2.getNickname(), profile2.getPoint(), getWinRate(0, 0, 0),
                profile2.getRank().getRankName(), redisCategory);
        long ranking3[] = createRankingMember(member3.getId(), profile3.getNickname(), profile3.getPoint(), getWinRate(0, 0, 0),
                profile3.getRank().getRankName(), redisCategory);
        long ranking4[] = createRankingMember(member4.getId(), profile4.getNickname(), profile4.getPoint(), getWinRate(0, 0, 0),
                profile4.getRank().getRankName(), redisCategory);
        long ranking5[] = createRankingMember(member5.getId(), profile5.getNickname(), profile5.getPoint(), getWinRate(0, 0, 0),
                profile5.getRank().getRankName(), redisCategory);
        long ranking6[] = createRankingMember(member6.getId(), profile6.getNickname(), profile6.getPoint(), getWinRate(0, 0, 0),
                profile6.getRank().getRankName(), redisCategory);
        long ranking7[] = createRankingMember(member7.getId(), profile7.getNickname(), profile7.getPoint(), getWinRate(0, 0, 0),
                profile7.getRank().getRankName(), redisCategory);
        long ranking8[] = createRankingMember(member8.getId(), profile8.getNickname(), profile8.getPoint(), getWinRate(0, 0, 0),
                profile8.getRank().getRankName(), redisCategory);

        // redis_user_info 생성
        String selectedAchievement = "selectedAchievement";
        List<String> achievements = List.of(selectedAchievement);
        createRedisUserInfo(profile1, selectedAchievement, redisCategory,
                ranking1, achievements, new ArrayList<>());
        createRedisUserInfo(profile2, selectedAchievement, redisCategory,
                ranking2, achievements, new ArrayList<>());
        createRedisUserInfo(profile3, selectedAchievement, redisCategory,
                ranking3, achievements, new ArrayList<>());
        createRedisUserInfo(profile4, selectedAchievement, redisCategory,
                ranking4, achievements, new ArrayList<>());
        createRedisUserInfo(profile5, selectedAchievement, redisCategory,
                ranking5, achievements, new ArrayList<>());
        createRedisUserInfo(profile6, selectedAchievement, redisCategory,
                ranking6, achievements, new ArrayList<>());
        createRedisUserInfo(profile7, selectedAchievement, redisCategory,
                ranking7, achievements, new ArrayList<>());
        createRedisUserInfo(profile8, selectedAchievement, redisCategory,
                ranking8, achievements, new ArrayList<>());
    }

    @Test
    void dbDataTest() {
        List<Profile> profiles = profileRepository.findAll();
        List<Member> members = memberRepository.findAll();

        assertThat(profiles.size()==7);
        assertThat(members.size()==8);

        for(Profile profile : profiles) {
            int profileMemberId = profile.getMember().getId();
            assertThat(memberRepository.existsById(profileMemberId));
        }
    }

    @Test
    void redisTest() {
        List<Profile> profiles = profileRepository.findAll();
        List<Member> members = memberRepository.findAll();
        HashOperations<String, DebateRedisKey, Object> debateHashOperations = redisTemplate.opsForHash();
        final String REDIS_PK = savedDebate.getDebateId() + "";

        DebateMembers debateMembers = (DebateMembers) debateHashOperations.get(REDIS_PK, DebateRedisKey.DEBATE_MEMBER_LIST);

        for(DebateMember debateMember : debateMembers.getLeftMembers()) {
            System.out.println(debateMember.getMemberId());
            assertThat(memberRepository.existsById(debateMember.getMemberId()));
        }
        for(DebateMember debateMember : debateMembers.getRightMembers()) {
            System.out.println(debateMember.getMemberId());
            assertThat(memberRepository.existsById(debateMember.getMemberId()));
        }
        for(DebateMember debateMember : debateMembers.getJurors()) {
            System.out.println(debateMember.getMemberId());
            assertThat(memberRepository.existsById(debateMember.getMemberId()));
        }
        for(DebateMember debateMember : debateMembers.getWatchers()) {
            System.out.println(debateMember.getMemberId());
            assertThat(memberRepository.existsById(debateMember.getMemberId()));
        }

        //========================================================================================================
        Profile profile = profiles.get(0);
        Member member = profile.getMember();

        System.out.println(member.getId());
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = redisTemplate.opsForHash();
        String redisUserInfoRankKey = member.getUserId() + ":" + Constants.RANK_REDIS_KEY;
        String redisUserInfoCategoryKey = member.getUserId() + ":" + constants.getConstantsByCategory(savedDebate.getDebateCategory().getValue());
//        List<String> achievements = (List<String>) memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.ACHIEVEMENTS);
        List<Record> records = (List<Record>) memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.LATEST_GAME_RECORD);

    }

    @Test
    void addGameTest() {
        AddGameRequestDto addGameRequestDto = AddGameRequestDto.builder().debateId(savedDebate.getDebateId()).build();
        gameService.addGame(addGameRequestDto);

        // 게임 정보
        System.out.println("\n[게임 정보]");
        for(GameInfo gameInfo : gameInfoRepository.findAll())
            System.out.println(gameInfo);

        // 전적
        System.out.println("\n[전적]");
        for(Record record : recordRepository.findAll())
            System.out.println(record);

        // redis_user_info
//        System.out.println("\n[Redis_User_Info]");
//        HashOperations<String, MemberRedisKey, Object> memberHashOperations = redisTemplate.opsForHash();
//        for(Profile profile : savedProfiles) {
//            showRedisUserInfo(profile);
//        }

        // redis_ranking
        System.out.println("\n[Redis_Ranking]");
        ZSetOperations<String, RankingMember> rankingMemberZSetOperations = rankingMemberRedisTemplate.opsForZSet();
        Set<RankingMember> range = rankingMemberZSetOperations.range(Constants.RANK_REDIS_KEY, 0, -1);
        Set<RankingMember> range2 = rankingMemberZSetOperations.range(Constants.RANK_ANIMAL_REDIS_KEY, 0, -1);
        for(RankingMember rankingMember : range)
            System.out.println(rankingMember);
        System.out.println("================================================");
        for(RankingMember rankingMember : range2)
            System.out.println(rankingMember);


        // Re
    }

    void showRedisUserInfo(Profile profile) {
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = redisTemplate.opsForHash();

        Member member = profile.getMember();
        String redisUserInfoRankKey = member.getUserId() + ":" + Constants.RANK_REDIS_KEY;
        String redisUserInfoCategoryKey = member.getUserId() + ":" + constants.getConstantsByCategory(savedDebate.getDebateCategory().getValue());
        StringBuilder sb = new StringBuilder();

        sb.append("<전체 UserInfo>");
        sb.append("REFRESH_TOKEN : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.REFRESH_TOKEN) + ", ");
        sb.append("SELECTED_ACHIEVEMENT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.SELECTED_ACHIEVEMENT) + ", ");
        sb.append("RANK_NAME : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.RANK_NAME) + ", ");
        sb.append("POINT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.POINT) + ", ");
        sb.append("WIN_COUNT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.WIN_COUNT) + ", ");
        sb.append("LOSE_COUNT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.LOSE_COUNT) + ", ");
        sb.append("TIE_COUNT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.TIE_COUNT) + ", ");
        sb.append("MVP_COUNT : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.MVP_COUNT) + ", ");
        sb.append("RANKING : " + memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.RANKING) + ", ");
        List<String> achievements = (List<String>) memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.ACHIEVEMENTS);
        sb.append("ACHIEVEMENTS : " + achievements + ", ");
        List<Record> records = (List<Record>) memberHashOperations.get(redisUserInfoRankKey, MemberRedisKey.LATEST_GAME_RECORD);
        sb.append("LATEST_GAME_RECORD : " + records + ", ");

        sb.append("\n<카테고리 UserInfo>");
        sb.append("REFRESH_TOKEN : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.REFRESH_TOKEN) + ", ");
        sb.append("SELECTED_ACHIEVEMENT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.SELECTED_ACHIEVEMENT) + ", ");
        sb.append("RANK_NAME : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.RANK_NAME) + ", ");
        sb.append("POINT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.POINT) + ", ");
        sb.append("WIN_COUNT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.WIN_COUNT) + ", ");
        sb.append("LOSE_COUNT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.LOSE_COUNT) + ", ");
        sb.append("TIE_COUNT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.TIE_COUNT) + ", ");
        sb.append("MVP_COUNT : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.MVP_COUNT) + ", ");
        sb.append("RANKING : " + memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.RANKING) + ", ");
        List<String> achievements2 = (List<String>) memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.ACHIEVEMENTS);
        sb.append("ACHIEVEMENTS : " + achievements2 + ", ");
        List<Record> records2 = (List<Record>) memberHashOperations.get(redisUserInfoCategoryKey, MemberRedisKey.LATEST_GAME_RECORD);
        sb.append("LATEST_GAME_RECORD : " + records2 + ", ");


        System.out.println(sb);
    }

    void createRedisUserInfo(Profile profile, String selectedAchievement, String redisCategory,
                             long[] ranking, List<String> achievements, List<Record> latestGameRecord) {
        HashOperations<String, MemberRedisKey, Object> memberHashOperations = redisTemplate.opsForHash();

        Member member = profile.getMember();
        String redisUserInfoRankKey = member.getUserId() + ":" + Constants.RANK_REDIS_KEY;
        String redisUserInfoCategoryKey = member.getUserId() + ":" + redisCategory;

        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.REFRESH_TOKEN, member.getUserId());
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.SELECTED_ACHIEVEMENT, selectedAchievement);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.RANK_NAME, profile.getRank().getRankName().getName());
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.POINT, profile.getPoint());
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.WIN_COUNT, 0);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.LOSE_COUNT, 0);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.TIE_COUNT, 0);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.MVP_COUNT, 0);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.RANKING, ranking[0]+1);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.ACHIEVEMENTS, achievements);
        memberHashOperations.put(redisUserInfoRankKey, MemberRedisKey.LATEST_GAME_RECORD, latestGameRecord);

        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.REFRESH_TOKEN, member.getUserId());
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.SELECTED_ACHIEVEMENT, selectedAchievement);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.RANK_NAME, profile.getRank().getRankName().getName());
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.POINT, profile.getPoint());
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.WIN_COUNT, 0);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.LOSE_COUNT, 0);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.TIE_COUNT, 0);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.MVP_COUNT, 0);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.RANKING, ranking[1]+1);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.ACHIEVEMENTS, achievements);
        memberHashOperations.put(redisUserInfoCategoryKey, MemberRedisKey.LATEST_GAME_RECORD, latestGameRecord);
    }

    long[] createRankingMember(Integer memberId, String nickname, int point, int winRate,
                                              RankName rankName, String redisKey){
        ZSetOperations<String, RankingMember> rankingMemberZSetOperations = rankingMemberRedisTemplate.opsForZSet();

        RankingMember rankingMember = RankingMember.builder()
                .memberId(memberId)
                .nickname(nickname)
                .winRate(winRate)
                .rankName(rankName)
                .achievement("selectedAchievement")
                .point(point)
                .build();

        rankingMemberZSetOperations.add(Constants.RANK_REDIS_KEY, rankingMember, -rankingMember.getPoint());
        rankingMemberZSetOperations.add(redisKey, rankingMember, -rankingMember.getPoint());

        Long redisRank = rankingMemberZSetOperations.rank(Constants.RANK_REDIS_KEY, rankingMember);
        Long redisCategoryRank = rankingMemberZSetOperations.rank(redisKey, rankingMember);

        return new long[] {redisRank, redisCategoryRank};
    }

    private int getWinRate(int win, int lose, int tie) {
        return win+lose==0
                ? 0
                : (int) ((double) win / (win + lose + tie) * 100.0);
    }
}