package com.a708.drwa.member.data.dto.request;

import com.a708.drwa.debate.enums.DebateCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateInterestRequestDto {
    @JsonProperty(value = "debateCategories")
    private List<DebateCategory> debateCategories;

    public UpdateInterestRequestDto(List<DebateCategory> debateCategories) {
        this.debateCategories = debateCategories;
    }
}
