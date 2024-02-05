package com.a708.drwa.ranking.service;

import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.ranking.domain.RankingMember;
import com.a708.drwa.ranking.dto.SearchByNicknameResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.a708.drwa.redis.constant.Constants.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.context.annotation.Profile("test")
@Transactional
class RankingServiceTest {
    @Autowired private RankingService rankingService;
    @Autowired private RedisTemplate<String, RankingMember> rankMemberRedisTemplate;
    private static final String REDIS_KEY = "rank";

    @BeforeEach
    public void redisDataInit(){
        rankMemberRedisTemplate.delete("rank");
        for(int i=100; i<=10000; i+=100){
            createRankingMember(i, "member" + i, i, 1, RankName.DIAMOND);
        }
    }


    @Test
    void findTop20Ranks() {
        // given

        // when
        List<RankingMember> top20Ranks = rankingService.findTop20Ranks();

        // then
        assertThat(top20Ranks.size()).isLessThanOrEqualTo(20);
        assertThat(top20Ranks.get(0).getPoint()).isEqualTo(10000);
        assertTrue(isSortedDescending(top20Ranks));
    }

    @Test
    void findTop10Bottom10() {
        // given

        // when
        SearchByNicknameResponse results = rankingService.findTop10Bottom10("member4000");

        // then
        assertThat(results.getMe().getNickname()).isEqualTo("member4000");
        assertThat(results.getTopAndBottom().size()).isEqualTo(20);
        assertThat(results.getTopAndBottom().get(0).getPoint()).isEqualTo(5000);
        assertThat(results.getTopAndBottom().get(19).getPoint()).isEqualTo(3000);
    }


    @Test
    void findTop20ByCategory(){
        // given
        Set<RankingMember> allRankingMembers = rankMemberRedisTemplate.opsForZSet().range(REDIS_KEY, 0, -1);

        int num = 100;
        for (RankingMember rankingMember : allRankingMembers) {
            rankMemberRedisTemplate.opsForZSet().remove(REDIS_KEY, rankingMember);
            rankMemberRedisTemplate.opsForZSet().remove(RANK_ANIMAL_REDIS_KEY, rankingMember);
            if(rankingMember.getMemberId() % 3 == 0)
                rankMemberRedisTemplate.opsForZSet().remove(RANK_ECONOMY_REDIS_KEY, rankingMember);
            rankingMember.increasePoint(num);
            rankMemberRedisTemplate.opsForZSet().add(REDIS_KEY, rankingMember, -(rankingMember.getPoint() + num));
            rankMemberRedisTemplate.opsForZSet().add(RANK_ANIMAL_REDIS_KEY, rankingMember, -(rankingMember.getPoint() + num));
            if(rankingMember.getMemberId() % 3 == 0)
                rankMemberRedisTemplate.opsForZSet().add(RANK_ECONOMY_REDIS_KEY, rankingMember, -(rankingMember.getPoint() + num));
        }

        // when
        List<RankingMember> animalRankings = rankingService.findTop20ByCategory("rank:animal");
        List<RankingMember> economyRankings = rankingService.findTop20ByCategory("rank:economy");

        // then
        assertThat(animalRankings.size()).isEqualTo(20);
        assertThat(animalRankings.get(0).getMemberId()).isEqualTo(10000);
        assertThat(animalRankings.get(0).getPoint()).isEqualTo(10100);

        assertThat(economyRankings.size()).isEqualTo(20);
        assertThat(economyRankings.get(0).getMemberId()).isEqualTo(9900);
        assertThat(economyRankings.get(0).getPoint()).isEqualTo(10000);

        assertThat(economyRankings.get(1).getMemberId()).isEqualTo(9600);

    }

    @Test
    void findOneAndUpdate() {
        ZSetOperations<String, RankingMember> stringRankingMemberZSetOperations = rankMemberRedisTemplate.opsForZSet();
        System.out.println(stringRankingMemberZSetOperations.size(REDIS_KEY));

        RankingMember rankingMember = RankingMember.builder()
                .memberId(5000)
                .nickname("member"+5000)
                .winRate(1)
                .rankName(RankName.DIAMOND)
                .point(5000)
                .build();
        RankingMember rankingMember2 = RankingMember.builder()
                .memberId(5000)
                .nickname("member"+5000)
                .winRate(5)
                .rankName(RankName.DIAMOND)
                .point(5000)
                .build();

        Long idx = stringRankingMemberZSetOperations.rank(REDIS_KEY, rankingMember);
        Long cnt = stringRankingMemberZSetOperations.remove(REDIS_KEY, rankingMember);
        System.out.println(idx + " " + cnt);
        assertThat(cnt.equals((long) 1));


        System.out.println(stringRankingMemberZSetOperations.reverseRank(REDIS_KEY, rankingMember2));

    }

    private RankingMember createRankingMember(Integer memberId, String nickname, int point, int winRate, RankName rankName){
        RankingMember rankingMember = RankingMember.builder()
                .memberId(memberId)
                .nickname(nickname)
                .winRate(winRate)
                .rankName(rankName)
//                .title(rankName + "칭호")
                .point(point)
                .build();
        rankMemberRedisTemplate.opsForZSet().add(REDIS_KEY, rankingMember, -rankingMember.getPoint());
        return rankingMember;
    }

    private boolean isSortedDescending(List<RankingMember> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).getPoint() < list.get(i + 1).getPoint()) {
                return false;
            }
        }
        return true;
    }
}