package com.a708.drwa.debate.repository;

import com.a708.drwa.debate.domain.Debate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebateRepository extends JpaRepository<Debate, Integer> {
}
