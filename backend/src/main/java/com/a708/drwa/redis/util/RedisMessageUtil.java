package com.a708.drwa.redis.util;

import com.a708.drwa.redis.message.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisMessageUtil {
    // ObjectMapper : JSON 형식을 사용할 때,
    // 응답들을 직렬화(Object -> String)
    // 요청들을 역직렬화(String -> Object)
    private final ObjectMapper objectMapper;

    // 이벤트
    private final String JOIN_EVENT = "join";
    private final String CHOOSE_POSITION_EVENT = "position";
    private final String SETTING_EVENT = "setting";
    private final String CHANGE_SETTING_EVENT = "changeSetting";
    private final String START_EVENT = "start";
    private final String WAITING_EVENT = "waiting";



    public String positionMessage(List<Integer> teamAList, List<Integer> teamBList, List<Integer> jurorList, List<Integer> viewerList) {
        DebatePositionMessage debatePositionMessage = new DebatePositionMessage();
        debatePositionMessage.setEvent(CHOOSE_POSITION_EVENT);
        debatePositionMessage.setTeamAList(teamAList);
        debatePositionMessage.setTeamBList(teamBList);
        debatePositionMessage.setJurorList(jurorList);
        debatePositionMessage.setViewerList(viewerList);
        try {
            String json = objectMapper.writeValueAsString(debatePositionMessage);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public String settingMessage(String title,String keyword, Boolean isPrivate, Integer password, Integer playerNum,
            Integer jurorNum, Integer opinionTime, Integer readyTime, Integer questionTime) {
        DebateSettingMessage debateSettingMessage = new DebateSettingMessage();
        debateSettingMessage.setEvent(SETTING_EVENT);
        debateSettingMessage.setTitle(title);
        debateSettingMessage.setKeyword(keyword);
        debateSettingMessage.setIsPrivate(isPrivate);
        if(isPrivate) debateSettingMessage.setPassword(password);
        debateSettingMessage.setPlayerNum(playerNum);
        debateSettingMessage.setJurorNum(jurorNum);
        debateSettingMessage.setOpinionTime(opinionTime);
        debateSettingMessage.setReadyTime(readyTime);
        debateSettingMessage.setQuestionTime(questionTime);
        try {
            String json = objectMapper.writeValueAsString(debateSettingMessage);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String changeSettingMessage(String title, String keyword, Boolean isPrivate, Integer password,
                                      Integer opinionTime, Integer readyTime, Integer questionTime) {
        DebateChangeSettingMessage debateChangeSettingMessage = new DebateChangeSettingMessage();
        debateChangeSettingMessage.setEvent(CHANGE_SETTING_EVENT);
        debateChangeSettingMessage.setTitle(title);
        debateChangeSettingMessage.setKeyword(keyword);
        debateChangeSettingMessage.setIsPrivate(isPrivate);
        if(isPrivate) debateChangeSettingMessage.setPassword(password);
        debateChangeSettingMessage.setOpinionTime(opinionTime);
        debateChangeSettingMessage.setReadyTime(readyTime);
        debateChangeSettingMessage.setQuestionTime(questionTime);
        try {
            String json = objectMapper.writeValueAsString(debateChangeSettingMessage);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String startMessage(Integer phase, String currentSpeaker, String mvp, Integer voteTeamA,Integer voteTeamB) {
        DebateStartMessage debateStartMessage = new DebateStartMessage();
        debateStartMessage.setEvent(START_EVENT);
        debateStartMessage.setPhase(phase);
        debateStartMessage.setCurrentSpeaker(currentSpeaker);
        debateStartMessage.setMvp(mvp);
        debateStartMessage.setVoteTeamA(voteTeamA);
        debateStartMessage.setVoteTeamB(voteTeamB);

        try {
            String json = objectMapper.writeValueAsString(debateStartMessage);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String debateWaitingMessage(List<String> teamAList, List<String> teamBList, List<String> jurorList, Integer viewerCnt) {
        DebateWaitingMessage debateWaitingMessage = new DebateWaitingMessage();
        debateWaitingMessage.setEvent(WAITING_EVENT);
        debateWaitingMessage.setTeamAList(teamAList);
        debateWaitingMessage.setTeamBList(teamBList);
        debateWaitingMessage.setJurorList(jurorList);
        debateWaitingMessage.setViewerCnt(viewerCnt);
        try {
            String json = objectMapper.writeValueAsString(debateWaitingMessage);
            return json;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
