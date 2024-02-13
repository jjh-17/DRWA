package com.a708.drwa.debate.data.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DebateInfoListResponse {
    List<DebateInfoResponse> debateInfoResponses;

    @Builder
    public DebateInfoListResponse(List<DebateInfoResponse> debateInfoResponses) {
        this.debateInfoResponses = debateInfoResponses;
    }
}
