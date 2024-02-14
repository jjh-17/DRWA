package com.a708.drwa.member.data.dto.response;

import com.a708.drwa.debate.enums.DebateCategory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInterestCategoriesResponse {
    private List<DebateCategory> debateCategories;

    @Builder
    public MemberInterestCategoriesResponse(List<DebateCategory> debateCategories) {
        this.debateCategories = debateCategories;
    }
}
