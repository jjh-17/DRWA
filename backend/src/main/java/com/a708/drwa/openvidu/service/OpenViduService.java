package com.a708.drwa.openvidu.service;


import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.openvidu.data.dto.request.CreateRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.response.CreateRoomResponseDto;
import com.a708.drwa.openvidu.data.dto.response.GetConnectionResponseDto;
import com.a708.drwa.openvidu.exception.OpenViduErrorCode;
import com.a708.drwa.openvidu.exception.OpenViduException;
import com.a708.drwa.openvidu.repository.DebateRoomRepository;
import com.a708.drwa.utils.JWTUtil;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenViduService {
    private final DebateRoomRepository debateRoomRepository;
    private final OpenVidu openVidu;
    private final JWTUtil jwtUtil;

    public CreateRoomResponseDto createSession(CreateRoomRequestDto createRoomDto, String jwt) {
        // get userId from jwt
        String userId = jwtUtil.getMember(jwt).getUserId();
        String sessionId = userId + "-" + UUID.randomUUID();
        SessionProperties sessionProperties = new SessionProperties.Builder()
                .customSessionId(sessionId)
                .build();

        // create openVidu session
        log.info(">>>>>>>>>> openVidu Service createSession userId : {} sessionId : {}", userId, sessionId);
        Session session = null;
        try {
            session = openVidu.createSession(sessionProperties);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            throw new OpenViduException(OpenViduErrorCode.OPENVIDU_CAN_NOT_CREATE_SESSION);
        }

        // save room info
        debateRoomRepository.save(createRoomDto.toEntity(sessionId));

        // return sessionId;
        return CreateRoomResponseDto.builder()
                .sessionId(sessionId)
                .build();
    }

    public GetConnectionResponseDto getConnection(String sessionId, String jwt) {
        log.info("----------- create connection START -----------");
        log.info("url 패스에 들어있는 session Id : {}", sessionId);
        log.info("JWT : {}", jwt);

        //이미 있는 세션을 sessionId를 통해 가져오게 된다.
        Session session = openVidu.getActiveSession(sessionId);

        //세션이 존재하지 않는다면 연결을 만들 수 없다.
        if (session == null || !debateRoomRepository.existsById(sessionId))
            throw new OpenViduException(OpenViduErrorCode.OPENVIDU_NOT_FOUND_SESSION);

        ConnectionProperties properties;
        Connection connection;

        //연결과 토큰 만들기
        JWTMemberInfo memberInfo = jwtUtil.getMember(jwt);

        // 로그인 된 userId와 sessionId가 같을 때
        try {
            if(sessionId.startsWith(memberInfo.getUserId()+"-"))
            {
                properties = new ConnectionProperties.Builder()
                        .role(OpenViduRole.PUBLISHER)
                        .build();
                connection = session.createConnection(properties);
            }else {
                properties = new ConnectionProperties.Builder()
                        .role(OpenViduRole.SUBSCRIBER)
                        .build();
                connection = session.createConnection(properties);
            }
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new OpenViduException(OpenViduErrorCode.OPENVIDU_CAN_NOT_CREATE_SESSION);
        }

        return GetConnectionResponseDto.builder()
                .connection(connection)
                .build();
    }
}
