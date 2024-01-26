package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.util.RedisUtil;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final RedisUtil redisUtil;

    @Transactional
    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        return debateRepository.save(debateCreateRequestDto.toEntity()).getDebateId();
    }

//    public String join(DebateJoinRequestDto debateJoinRequestDto) {
//
//    }

    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }

    public void nextPhase(int debateId) {

    }
}
