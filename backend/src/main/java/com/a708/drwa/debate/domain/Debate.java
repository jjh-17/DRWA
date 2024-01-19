package com.a708.drwa.debate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Debate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int debateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debate_category_id")
    private DebateCategory debateCategory;

    @Builder
    public Debate(DebateCategory debateCategory) {
        this.debateCategory = debateCategory;
    }
}
