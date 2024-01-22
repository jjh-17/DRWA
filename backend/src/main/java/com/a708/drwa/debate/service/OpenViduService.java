package com.a708.drwa.debate.service;

import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OpenViduService {

    private final OpenVidu openVidu;

    private Map<Long, Session> openViduSessions = new ConcurrentHashMap<>();


////   방 생성
//     @param debateId
//     @return
//     @throws OpenViduJavaClientException
//     @throws OpenViduHttpException
    public void createSession(Long debateId) throws OpenViduJavaClientException, OpenViduHttpException {

        Session session = openVidu.createSession();
        Session findSession = openViduSessions.get(debateId);
        if(findSession!=null){
            // 이미 같은세션의 방이 있을 때
            throw new DebateException(DebateErrorCode.ALREADY_EXIST_DEBATE_ROOM_ERROR);
        }

        openViduSessions.put(debateId, session);

    }

////   방 입장 시 세션을 얻는 메소드
//     @param debateId
//     @return
    public String enterSession(Long debateId) throws OpenViduJavaClientException, OpenViduHttpException {

        Session session = openViduSessions.get(debateId);
        if(session == null){
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        }

        String Token = craeteToken(session);

        return Token;
    }

////     OpenVidu Server 입장을 위한 토큰 발급
//     @param session
//     @return
//     @throws OpenViduJavaClientException
//     @throws OpenViduHttpException
    private String craeteToken(Session session) throws OpenViduJavaClientException, OpenViduHttpException {
        ConnectionProperties connectionProperties =
                new ConnectionProperties.Builder()
                        .type(ConnectionType.WEBRTC).build();

        String Token = session.createConnection(connectionProperties).getToken();
        return Token;
    }

}