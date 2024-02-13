package com.a708.drwa.debate.domain;

import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.enums.DebateCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash("DebateRoom")
public class DebateRoomInfo {
    @Id
    private String sessionId;

    // 토론 카테고리
    @Indexed
    private DebateCategory debateCategory;

    // 방장 아이디
    private String hostName;

    // 토론 방 제목
    private String title;

    // 제시어 1, 2
    private String leftKeyword;
    private String rightKeyword;

    // 참여 인원 제한
    private int playerNum;
    private int jurorNum;

    // 비공개 시 비번 설정
    private Boolean isPrivate;
    private String password;

    // 페이즈 별 시간
    private int speakingTime;
    private int readyTime;
    private int qnaTime;

    // 썸네일
    private String thumbnail1;
    private String thumbnail2;

    private int totalCnt;

    public DebateInfoResponse toResponse() {
        return DebateInfoResponse.builder()
                .sessionId(this.sessionId)
                .debateCategory(this.debateCategory)
                .hostName(this.hostName)
                .title(this.title)
                .leftKeyword(this.leftKeyword)
                .rightKeyword(this.rightKeyword)
                .playerNum(this.playerNum)
                .jurorNum(this.jurorNum)
                .isPrivate(this.isPrivate)
                .password(this.password)
                .speakingTime(this.speakingTime)
                .readyTime(this.readyTime)
                .qnaTime(this.qnaTime)
                .thumbnail1(this.thumbnail1)
                .thumbnail2(this.thumbnail2)
                .totalCnt(this.totalCnt)
                .build();
    }
}
