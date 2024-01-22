package com.a708.drwa.rank.service;

import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.dto.response.RankResponse;
import com.a708.drwa.rank.enums.RankName;
import com.a708.drwa.rank.exception.RankErrorCode;
import com.a708.drwa.rank.repository.RankRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankService {
    private final RankRepository rankRepository;

    @Transactional
    public void initAllRanks(){
        List<Rank> allRanks = rankRepository.findAll();
        if(!allRanks.isEmpty()) return;
        List<Rank> ranks = createAllRanks();
        rankRepository.saveAll(ranks);
    }

    @Transactional
    public void clearAndInitAllRanks(){
        rankRepository.deleteAll();
        List<Rank> ranks = createAllRanks();
        rankRepository.saveAll(ranks);
    }

    public Rank findByRankName(RankName rankName){
        return rankRepository.findByRankName(rankName)
                .orElseThrow(() -> new GlobalException(RankErrorCode.RANK_NOT_FOUND));

    }

    public List<Rank> findAllRanks(){
        return rankRepository.findAll();
    }

    public List<RankResponse> findAllRanksWithDto(){
        return findAllRanks().stream()
                .map(r -> RankResponse.builder()
                        .rankId(r.getId())
                        .rankName(r.getRankName())
                        .pivotPoint(r.getPivotPoint())
                        .build())
                .collect(Collectors.toList());
    }

    private Rank createRank(RankName rankName, int pivotPoint) {
        return Rank.builder()
                .rankName(rankName)
                .pivotPoint(pivotPoint)
                .build();
    }

    private List<Rank> createAllRanks(){
        Rank bronze = createRank(RankName.BRONZE, 1000);
        Rank silver = createRank(RankName.SILVER, 1500);
        Rank gold = createRank(RankName.GOLD, 2000);
        Rank platinum = createRank(RankName.PLATINUM, 2500);
        Rank diamond = createRank(RankName.DIAMOND, 3000);
        Rank master = createRank(RankName.MASTER, 3500);
        Rank challenger = createRank(RankName.CHALLENGER, 4000);

        return List.of(bronze, silver, gold, platinum, diamond, master, challenger);
    }

}
