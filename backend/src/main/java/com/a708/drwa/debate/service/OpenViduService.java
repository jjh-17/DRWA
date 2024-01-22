package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OpenViduService {
    private final OpenVidu openVidu;
    private final Map<Integer, Session> openViduSessions = new ConcurrentHashMap<>();

    /**
     * 토론 방 openVidu Session 생성
     * debateId -> createSession in openVidu server
     * -> if not exist -> add session to openViduSessions
     * @param debateId
     */
    public void create(int debateId) {
        Session session = null;

        try {
            session = openVidu.createSession();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new DebateException(DebateErrorCode.OPENVIDU_INTERNAL_ERROR);
        }

        if(openViduSessions.get(debateId) != null) {
            throw new DebateException(DebateErrorCode.ALREADY_EXIST_DEBATE_ROOM_ERROR);
        }

        openViduSessions.put(debateId, session);
    }

    /**
     * 토론 방 입장 시 해당 방의 Session 반환
     * debateId -> get Session from openViduSessions
     * -> create token from session -> return token
     * @param debateId
     * @return token
     */
    public String join(int debateId) {
        Session session = openViduSessions.get(debateId);

        if(session == null)
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);

        return createToken(session);
    }

    /**
     * 토큰 생성 함수
     * create connection properties from session
     * -> create connection by generated properties
     * -> getToken through connection
     * -> return token
     * @param session
     * @return token
     */
    public String createToken(Session session) {
        ConnectionProperties connectionProperties =
                new ConnectionProperties.Builder()
                        .type(ConnectionType.WEBRTC)
                        .build();
        String token = "";
        try {
            token = session.createConnection(connectionProperties).getToken();
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new DebateException(DebateErrorCode.OPENVIDU_INTERNAL_ERROR);
        }

        return token;
    }
}
