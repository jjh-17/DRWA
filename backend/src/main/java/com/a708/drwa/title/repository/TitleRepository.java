package com.a708.drwa.title.repository;

import com.a708.drwa.title.domain.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TitleRepository extends JpaRepository<Title, Integer> {
    Optional<Title> findByName(String name);
}
