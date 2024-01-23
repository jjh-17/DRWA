package com.a708.drwa.debate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DebateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int debateCategoryId;

    @Column(updatable = false)
    private String debateCategoryName;
}
