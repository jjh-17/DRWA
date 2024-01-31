package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DebateCreateRequestDto {
    private DebateCategory debateCategory;
}
