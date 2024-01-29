package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.domain.DebateCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DebateCreateRequestDto {
    private DebateCategory debateCategory;

    // 토론 id
    private int debateId;

    // 현재 단계 방 생성 : 0단계
    private int phase;

    // 제목 , 제시어, 방장
    private int title;
    private int keywordA;
    private int keywordB;
    private int host;

    // 공개방? 비밀방?
    private boolean isPrivate;
    private int password;

    // 플레이어수, 배심원수
    private int playerNum;
    private int jurorNum;

    // 준비시간, 발언시간, 질문시간
    private int opinionTime;
    private int readyTime;
    private int questionTime;

    // 방에 들어와있는 총 인원수 ->  생성시 :0명
    private int totalNum;
}
