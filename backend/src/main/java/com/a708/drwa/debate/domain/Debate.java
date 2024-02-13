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

    private String sessionId;

    @Enumerated
    private DebateCategory debateCategory;

    private int totalCnt;

    @Builder
    public Debate(String sessionId, DebateCategory debateCategory) {
        this.sessionId = sessionId;
        this.debateCategory = debateCategory;
        this.totalCnt = 1;
    }
}
