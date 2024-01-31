package com.a708.drwa.debate.domain;

import com.a708.drwa.debate.enums.DebateCategory;
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

    @Enumerated
    private DebateCategory debateCategory;

    @Builder
    public Debate(DebateCategory debateCategory) {
        this.debateCategory = debateCategory;
    }
}
