package com.a708.drwa.member.data.dto.request;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateInterestRequestDto {
    private List<DebateCategory> debateCategories;

    public UpdateInterestRequestDto(List<DebateCategory> debateCategories) {
        this.debateCategories = debateCategories;
    }
}
