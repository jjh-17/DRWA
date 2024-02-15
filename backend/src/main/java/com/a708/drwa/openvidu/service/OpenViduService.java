package com.a708.drwa.openvidu.service;


import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.util.DebateUtil;
import com.a708.drwa.global.exception.GlobalException;
import com.a708.drwa.member.data.JWTMemberInfo;
import com.a708.drwa.openvidu.data.dto.request.CreateRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.request.JoinRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.request.LeaveRoomRequestDto;
import com.a708.drwa.openvidu.data.dto.response.CreateRoomResponseDto;
import com.a708.drwa.openvidu.data.dto.response.GetConnectionResponseDto;
import com.a708.drwa.openvidu.exception.OpenViduErrorCode;
import com.a708.drwa.openvidu.exception.OpenViduException;
import com.a708.drwa.debate.repository.DebateRoomRepository;
import com.a708.drwa.profile.exception.ProfileErrorCode;
import com.a708.drwa.profile.repository.ProfileRepository;
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
    private final ProfileRepository profileRepository;
    private final OpenVidu openVidu;
    private final JWTUtil jwtUtil;
    private final DebateUtil debateUtil;

    public CreateRoomResponseDto createSession(CreateRoomRequestDto createRoomDto, String token) {
        // get userId from jwt
        JWTMemberInfo memberInfo = jwtUtil.getMember(token);
        String userId = memberInfo.getUserId();
        String nickName = profileRepository.findByMemberId(memberInfo.getMemberId())
                .orElseThrow(() -> new GlobalException(ProfileErrorCode.PROFILE_NOT_FOUND))
                .getNickname();

        // create session properties
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
        debateRoomRepository.save(createRoomDto.toEntity(sessionId, nickName));

        // return sessionId;
        return CreateRoomResponseDto.builder()
                .sessionId(sessionId)
                .build();
    }

    public GetConnectionResponseDto getConnection(JoinRoomRequestDto joinRoomRequestDto, String jwt) {
        log.info("----------- create connection START -----------");
        log.info("url 패스에 들어있는 session Id : {}", joinRoomRequestDto.getSessionId());
        log.info("JWT : {}", jwt);
        int memberId = jwtUtil.getMember(jwt).getMemberId();
        return debateUtil.join(
                joinRoomRequestDto.getSessionId(),
                joinRoomRequestDto.getRole(),
                DebateMember.builder()
                        .memberId(memberId)
                        .nickName(joinRoomRequestDto.getNickName())
                        .role(joinRoomRequestDto.getRole())
                        .build());
    }

    public void disconnect(LeaveRoomRequestDto leaveRoomRequestDto, String jwt) {
        int memberId = jwtUtil.getMember(jwt).getMemberId();
        debateUtil.leave(
                leaveRoomRequestDto.getSessionId(),
                leaveRoomRequestDto.getRole(),
                DebateMember.builder()
                        .memberId(memberId)
                        .nickName(leaveRoomRequestDto.getNickName())
                        .role(leaveRoomRequestDto.getRole())
                        .build()
        );
    }
}
