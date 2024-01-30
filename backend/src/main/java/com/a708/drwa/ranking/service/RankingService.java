package com.a708.drwa.ranking.service;

import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.member.exception.MemberErrorCode;
import com.a708.drwa.ranking.dto.SearchByNicknameResponse;
import com.a708.drwa.rank.exception.RankErrorCode;
import com.a708.drwa.ranking.domain.RankingMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.a708.drwa.redis.constant.Constants.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RankingService {
    private final RedisTemplate<String, RankingMember> rankMemberRedisTemplate;

    public List<RankingMember> findTop20Ranks(){
        Set<RankingMember> members = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, 19);
        if(members == null) {
            throw new GlobalException(RankErrorCode.RANK_NOT_FOUND);
        }

        return members.stream().toList();
    }

    public SearchByNicknameResponse findTop10Bottom10(String nickname){
        Set<RankingMember> members = rankMemberRedisTemplate.opsForZSet().range(RANK_REDIS_KEY, 0, -1);
        RankingMember rankingMember = members.stream()
                .filter(m -> m.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new GlobalException(MemberErrorCode.MEMBER_NOT_FOUND));

        Double memberScore = rankMemberRedisTemplate.opsForZSet().score(RANK_REDIS_KEY, rankingMember);

        Set<ZSetOperations.TypedTuple<RankingMember>> bottomMembers = rankMemberRedisTemplate.opsForZSet().rangeByScoreWithScores(RANK_REDIS_KEY, memberScore, Double.POSITIVE_INFINITY, 1, 10);
        Set<ZSetOperations.TypedTuple<RankingMember>> topMembers = rankMemberRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(RANK_REDIS_KEY, Double.NEGATIVE_INFINITY, memberScore, 1, 10);

        List<RankingMember> rankingWithNeighbors = Stream.concat(
                    topMembers.stream().sorted(),
                    bottomMembers.stream())
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());
        return SearchByNicknameResponse.builder()
                .me(rankingMember)
                .topAndBottom(rankingWithNeighbors)
                .build();
    }

    public List<RankingMember> findTop20ByCategory(String category){
        Set<RankingMember> rankingMembers = rankMemberRedisTemplate.opsForZSet().range(category, 0, 19);
        if(rankingMembers == null) {
            throw new GlobalException(RankErrorCode.RANK_NOT_FOUND);
        }

        return rankingMembers.stream().toList();

    }
}
