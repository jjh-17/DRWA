package com.a708.drwa.member.data.dto;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class InterestDto {
    @Setter
    private DebateCategory debateCategory;
}
