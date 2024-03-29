package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Integer> {
    List<Record> findByMemberId(Integer memberId);
}
