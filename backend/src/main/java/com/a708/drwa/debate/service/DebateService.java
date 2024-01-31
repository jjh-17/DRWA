package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final DebateCategoryRepository debateCategoryRepository;
    private final RedisUtil redisUtil;
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

        return debateRepository.save(Debate.builder()
                .debateCategory(debateCategory).build()).getDebateId();
    }

    public void start(int debateId) {
        redisUtil.hSet(debateId + "", DebateRedisKey.START_TIME + "", System.currentTimeMillis() / 1000L + "");
        String leftUserKey = redisKeyUtil.getKeyByRoomIdWithKeyword(debateId, DebateRedisKey.TEAM_A_LIST);
        String rightUserKey = redisKeyUtil.getKeyByRoomIdWithKeyword(debateId, DebateRedisKey.TEAM_B_LIST);

//        List<Object> oLeftUserList = redisUtil.hGet()

    }


    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }
}
