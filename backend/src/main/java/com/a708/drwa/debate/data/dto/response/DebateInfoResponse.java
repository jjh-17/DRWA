package com.a708.drwa.debate.data.dto.response;

import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DebateInfoResponse {
    private String sessionId;
    private String hostName;
    private String title;
    private DebateCategory debateCategory;
    private String leftKeyword;
    private String rightKeyword;
    private String thumbnail1;
    private String thumbnail2;
    private int totalCnt;

    @Builder
    public DebateInfoResponse(
            String sessionId,
            String hostName,
            String title,
            DebateCategory debateCategory,
            String leftKeyword,
            String rightKeyword,
            String thumbnail1,
            String thumbnail2,
            int totalCnt
    ) {
        this.sessionId = sessionId;
        this.hostName = hostName;
        this.title = title;
        this.debateCategory = debateCategory;
        this.leftKeyword = leftKeyword;
        this.rightKeyword = rightKeyword;
        this.thumbnail1 = thumbnail1;
        this.thumbnail2 = thumbnail2;
        this.totalCnt = totalCnt;
    }
}
