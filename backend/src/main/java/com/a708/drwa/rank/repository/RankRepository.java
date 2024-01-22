package com.a708.drwa.rank.repository;

import com.a708.drwa.rank.domain.Rank;
import com.a708.drwa.rank.enums.RankName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RankRepository extends JpaRepository<Rank, Integer> {
    Optional<Rank> findByRankName(RankName rankName);
}
