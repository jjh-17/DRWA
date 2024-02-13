package com.a708.drwa.debate.data.dto.response;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Top5DebatesResponse {
    List<DebateInfoResponse> debateInfoResponses;

    @Builder
    public Top5DebatesResponse(List<DebateInfoResponse> debateInfoResponses) {
        this.debateInfoResponses = debateInfoResponses;
    }
}
