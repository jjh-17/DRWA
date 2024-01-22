package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.domain.DebateCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DebateCreateRequestDto {
    private DebateCategory debateCategory;

    public Debate toEntity() {
        return Debate.builder()
                .debateCategory(debateCategory)
                .build();
    }
}
