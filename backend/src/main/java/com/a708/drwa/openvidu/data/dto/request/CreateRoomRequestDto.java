package com.a708.drwa.openvidu.data.dto.request;


import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateRoomRequestDto {
    // 토론 카테고리
    private DebateCategory debateCategory;
    // 방장 아이디
    private int hostId;

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

    public DebateRoomInfo toEntity(String sessionId, String hostName) {
        return DebateRoomInfo.builder()
                .sessionId(sessionId)
                .debateCategory(this.debateCategory)
                .hostName(hostName)
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
                .totalCnt(0)
                .build();
    }
}
