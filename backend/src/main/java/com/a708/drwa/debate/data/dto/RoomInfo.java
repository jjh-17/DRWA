package com.a708.drwa.debate.data.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class RoomInfo {
    // 토론 카테고리
    private int debateCategoryId;

    // 방장 아이디
    private int hostId;

    // 토론 방 제목
    private String debateTitle;

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
}
