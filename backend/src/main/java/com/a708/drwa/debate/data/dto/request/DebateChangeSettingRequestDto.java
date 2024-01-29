package com.a708.drwa.debate.data.dto.request;

import com.a708.drwa.debate.domain.DebateCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DebateChangeSettingRequestDto {
    private DebateCategory debateCategory;

    // 토론 id
    private int debateId;

    // 제목 , 제시어
    private int title;
    private int keywordA;
    private int keywordB;

    // 공개방? 비밀방?
    private boolean isPrivate;
    private int password;


    // 준비시간, 발언시간, 질문시간
    private int opinionTime;
    private int readyTime;
    private int questionTime;
}
