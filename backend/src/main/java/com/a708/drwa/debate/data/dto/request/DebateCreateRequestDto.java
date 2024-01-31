package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DebateCreateRequestDto {
    private DebateCategory debateCategory;
}
