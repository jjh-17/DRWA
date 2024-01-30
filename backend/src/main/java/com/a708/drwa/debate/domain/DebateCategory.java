package com.a708.drwa.debate.domain;

import com.a708.drwa.debate.enums.DebateCategoryName;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DebateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "debate_category_id")
    private int debateCategoryId;

    @Enumerated(value = EnumType.STRING)
    private DebateCategoryName debateCategoryName;
}
