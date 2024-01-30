package com.a708.drwa.debate.data.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class DebateStartRequestDto {
    private int debateId;
    private int debateCategoryId;

    // 제목 , 제시어, 방장
    private String title;
    private String keywordA;
    private String keywordB;
    private int host;

    // 공개방? 비밀방?
    private boolean isPrivate;
    private int password;

    // 플레이어수, 배심원수
    private int playerNum;
    private int jurorNum;

    // 플레이어, 배심원, 관전자 리스트, 전체 리스트
    private List<Integer> teamAList;
    private List<Integer> teamBList;
    private List<Integer> jurorList;
    private List<Integer> viewerList;

    // 준비시간, 발언시간, 질문시간
    private int opinionTime;
    private int readyTime;
    private int questionTime;

    // 방에 들어와있는 총 인원수
    private int totalNum;
}
