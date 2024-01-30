package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.*;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.domain.DebateCategory;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateCategoryRepository;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.service.RedisPubService;
import com.a708.drwa.redis.util.RedisChannelUtil;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisMessageUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final DebateCategoryRepository debateCategoryRepository;
    private final RedisPubService redisPubService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final RedisMessageUtil redisMessageUtil;
    private final RedisChannelUtil redisChannelUtil;


    @Transactional
    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        DebateCategory debateCategory = debateCategoryRepository.findById(debateCreateRequestDto.getDebateCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("DebateCategory not found for id: " + debateCreateRequestDto.getDebateCategoryId()));

        Debate savedDebate = debateRepository.save(new Debate(debateCategory));
        int debateId = savedDebate.getDebateId();

        return debateId;
    }


    @Transactional
    public void join(DebateJoinRequestDto debateJoinRequestDto) {
        int debateId = debateJoinRequestDto.getDebateId();

        if(!debateRepository.existsById(debateId))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);

    }

    @Transactional
    public void start(DebateStartRequestDto debateStartRequestDto) {
        int debateId = debateStartRequestDto.getDebateId();

        DebateRoomInfoDto debateRoomInfo = new DebateRoomInfoDto(
                debateStartRequestDto.getDebateCategoryId(),
                debateStartRequestDto.getTitle(),
                debateStartRequestDto.getKeywordA(),
                debateStartRequestDto.getKeywordB(),
                debateStartRequestDto.getHost(),
                debateStartRequestDto.isPrivate(),
                debateStartRequestDto.getPassword(), // 비공개 토론인 경우 설정
                debateStartRequestDto.getPlayerNum(),
                debateStartRequestDto.getJurorNum(),
                debateStartRequestDto.getTeamAList(),
                debateStartRequestDto.getTeamBList(),
                debateStartRequestDto.getJurorList(),
                debateStartRequestDto.getViewerList(),
                debateStartRequestDto.getOpinionTime(),
                debateStartRequestDto.getReadyTime(),
                debateStartRequestDto.getQuestionTime(),
                debateStartRequestDto.getTotalNum()
        );

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        String key = "DebateId:" + debateId; // Redis에서 사용할 키

        valueOperations.set(key, debateRoomInfo);
    }


//    @Transactional
//    public boolean changeRole(DebateRoleRequestDto debateRoleRequestDto) {
//        int debateId = debateRoleRequestDto.getDebateId();
//        int userId = debateRoleRequestDto.getUserId();
//        int clickRole = debateRoleRequestDto.getClickRole();
//
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
//        try {
//            int teamSize = (int) valueOperations.get(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.PLAYER_NUM)) / 2;
//            int jurorSize = (int) valueOperations.get(redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_NUM));
//            String viewerListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VIEWER_LIST);
//            String teamAListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_A_LIST);
//            String teamBListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_B_LIST);
//            String jurorListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_LIST);
//
//            if (clickRole == 0) {
//                Long size = redisTemplate.opsForList().size(teamAListKey);
//                if (size >= teamSize) {
//                    return false;
//                }
//                stringObjectListOperations.rightPush(teamAListKey, userId);
//            } else if (clickRole == 1) {
//                Long size = redisTemplate.opsForList().size(teamBListKey);
//                if (size >= teamSize) {
//                    return false;
//                }
//                stringObjectListOperations.rightPush(teamBListKey, userId);
//            } else if (clickRole == 2) {
//                Long size = redisTemplate.opsForList().size(jurorListKey);
//                if (size >= jurorSize) {
//                    return false;
//                }
//                stringObjectListOperations.rightPush(jurorListKey, userId);
//            } else {
//                stringObjectListOperations.rightPush(viewerListKey, userId);
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }



}
