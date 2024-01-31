package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.DebateMemberDto;
import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.domain.DebateCategory;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateCategoryRepository;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisUtil;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final DebateCategoryRepository debateCategoryRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;

    /**
     * 토론 방 생성
     * @param debateCreateRequestDto
     * @return
     */
    @Transactional
    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        // 카테고리 가져옴
        DebateCategory debateCategory = debateCategoryRepository
                .findById(debateCreateRequestDto.getDebateCategoryId())
                .orElse(null);

        // 방 저장 및 아이디 추출
        return debateRepository.save(Debate.builder()
                        .debateCategory(debateCategory).build()).getDebateId();
    }

    public void start(DebateStartRequestDto debateStartRequestDto) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();

        String debateKey = debateStartRequestDto.getDebateId() + "";
        String startTimeKey = DebateRedisKey.START_TIME.string();
        String roomInfoKey = DebateRedisKey.ROOM_INFO.string();

        // 시작 시간 및 설정 저장
        hashOperations.put(debateKey, startTimeKey, System.currentTimeMillis() / 1000L + "");
        hashOperations.put(debateKey, roomInfoKey, debateStartRequestDto.getRoomInfo());

        // 유저 리스트 저장
        for(Map.Entry<Integer, DebateMemberDto> member : debateStartRequestDto.getMemberDtoHashMap().entrySet()) {
            DebateMemberDto memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        // 준비 단계로 PHASE 진행
        hashOperations.increment(debateKey, DebateRedisKey.PHASE.string(), 1);
    }


    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }
}
