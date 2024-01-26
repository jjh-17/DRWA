package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.domain.Debate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DebateCreateRequestDto {
    // 토론 카테고리
    private int debateCategoryId;

    // 토론 방 제목
    private String debateTitle;

    // 제시어 1, 2
    private String leftKeyword;
    private String rightKeyword;

    // 참여 인원 제한
    private int peopleCnt;

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
