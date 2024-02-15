package com.a708.drwa.debate.util;


import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import com.a708.drwa.debate.enums.Role;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRoomRepository;
import com.a708.drwa.openvidu.data.dto.response.GetConnectionResponseDto;
import com.a708.drwa.openvidu.exception.OpenViduErrorCode;
import com.a708.drwa.openvidu.exception.OpenViduException;
import com.a708.drwa.redis.util.RedisKeyUtil;
import io.openvidu.java.client.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DebateUtil {
    private final OpenVidu openVidu;
    private final RedisTemplate<String, DebateMember> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final DebateRoomRepository debateRoomRepository;

    public GetConnectionResponseDto join(String sessionId, Role role, DebateMember debateMember) {
        // 방 정보 조회
        DebateRoomInfo debateRoomInfo = debateRoomRepository.findById(sessionId)
                .orElseThrow(() -> new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR));
        log.info("debateUtil:join >> room find ! {}", debateRoomInfo);

        // 키 생성
        String key = redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, role.name());

        // 인원 다 차면 입장 못함
        Session session = openVidu.getActiveSession(sessionId);
        if(session == null)
            throw new OpenViduException(OpenViduErrorCode.OPENVIDU_NOT_FOUND_SESSION);
        log.info("debateUtil:join >> session is Active !\n");

        // 입장 및 연결
        ConnectionProperties properties;
        Connection connection;
        switch (role.ordinal()) {
            case 0:
            case 1:
                properties = new ConnectionProperties.Builder()
                        .role(OpenViduRole.PUBLISHER)
                        .build();
                joinAsDebator(key, debateRoomInfo.getPlayerNum(), debateMember);
                break;
            case 2:
                properties = new ConnectionProperties.Builder()
                        .role(OpenViduRole.SUBSCRIBER)
                        .build();
                joinAsDebator(key, debateRoomInfo.getJurorNum(), debateMember);
                break;
            case 3:
                properties = new ConnectionProperties.Builder()
                        .role(OpenViduRole.SUBSCRIBER)
                        .build();
                joinAsWatcher(key, debateMember);
                break;
            default:
                throw new DebateException(DebateErrorCode.INVALID_ROLE);
        }
        log.info("debateUtil:join >> save info success !\n");

        try {
            connection = session.createConnection(properties);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            throw new OpenViduException(OpenViduErrorCode.OPENVIDU_CAN_NOT_CREATE_CONNECTION);
        }
        log.info("debateUtil:join >> connect successfully ! connectionId : {}", connection.getConnectionId());

        // 인원 증가
        debateRoomInfo.join();
        log.info("debateUtil:join >> current user cnt : {}", debateRoomInfo.getTotalCnt());

        // response 만들어주자
        DebateInfoResponse debateInfoResponse = debateRoomInfo.toResponse();
        debateInfoResponse.setLists(DebateMembers.builder()
                .teamAMembers(getTeamAMembers(sessionId))
                .teamBMembers(getTeamBMembers(sessionId))
                .jurors(getJurors(sessionId))
                .watchers(getWatchers(sessionId))
                .build());

        return GetConnectionResponseDto.builder()
                .connection(connection)
                .debateInfoResponse(debateInfoResponse)
                .build();
    }

    public void leave(String sessionId, Role role, DebateMember debateMember) {
        // 방 정보 조회
        DebateRoomInfo debateRoomInfo = debateRoomRepository.findById(sessionId)
                .orElseThrow(() -> new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR));
        log.info("debateUtil:leave >> room find ! {}", debateRoomInfo);

        // 키 생성
        String key = redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, role.name());

        // redis list에서 해당 참여자 제거
        Long prevCnt = redisTemplate.opsForList().size(key);
        log.info("debateUtil:leave >> current member role : {}, current member cnt : {}", role.name(), prevCnt);

        // 삭제 실패 시 에러
        if(redisTemplate.opsForList().remove(key, 1, debateMember) == 0)
            throw new DebateException(DebateErrorCode.LEAVE_FAIL);

        Long postCnt = redisTemplate.opsForList().size(key);
        log.info("debateUtil:leave >> after leave member cnt : {}", postCnt);

        // roomInfo 갱신
        debateRoomInfo.exit();
    }

    /**
     * 참여자 혹은 배심원으로 입장
     * @param key 리스트 키
     * @param limit 인원 제한 수
     * @param debateMember 입장 인원
     */
    public void joinAsDebator(String key, int limit, DebateMember debateMember) {
        if(redisTemplate.hasKey(key) && redisTemplate.opsForList().size(key) > limit) throw new DebateException(DebateErrorCode.CAN_NOT_ENTER_AS_DEBATER);
        redisTemplate.opsForList().rightPush(key, debateMember);
    }

    /**
     * 관전자로 입장
     * @param key 리스트 키
     * @param debateMember 입장 인원
     */
    public void joinAsWatcher(String key, DebateMember debateMember) {
        redisTemplate.opsForList().rightPush(key, debateMember);
    }

    /**
     * 각 member List 가져오기
     * @param sessionId
     * @return
     */
    public List<DebateMember> getTeamAMembers(String sessionId) {
        if(!redisTemplate.hasKey(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.LEFT.name()))) return null;
        return redisTemplate.opsForList().range(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.LEFT.name()), 0, -1);
    }

    public List<DebateMember> getTeamBMembers(String sessionId) {
        if(!redisTemplate.hasKey(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.RIGHT.name()))) return null;
        return redisTemplate.opsForList().range(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.RIGHT.name()), 0, -1);
    }

    public List<DebateMember> getJurors(String sessionId) {
        if(!redisTemplate.hasKey(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.JUROR.name()))) return null;
        return redisTemplate.opsForList().range(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.JUROR.name()), 0, -1);
    }

    public List<DebateMember> getWatchers(String sessionId) {
        if(!redisTemplate.hasKey(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.WATCHER.name()))) return null;
        return redisTemplate.opsForList().range(redisKeyUtil.getKeyByRoomIdWithKeyword(sessionId, Role.WATCHER.name()), 0, -1);
    }
}
