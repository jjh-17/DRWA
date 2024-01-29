package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.DebateChangeSettingRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.service.RedisPubService;
import com.a708.drwa.redis.util.RedisChannelUtil;
import com.a708.drwa.redis.util.RedisKeyUtil;
import com.a708.drwa.redis.util.RedisMessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final RedisPubService redisPubService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final RedisMessageUtil redisMessageUtil;
    private final RedisChannelUtil redisChannelUtil;


    @Transactional
    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        int debateId = debateCreateRequestDto.getDebateId();

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String phaseKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.PHASE);
        String hostKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.HOST);
        String titleKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TITLE);
        String keywordAKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_A);
        String keywordBKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_B);
        String isPrivateKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.IS_PRIVATE);
        String passwordKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.PASSWORD);
        String playerNumKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.PLAYER_NUM);
        String jurorNumKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_NUM);
        String opinionTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.OPINION_TIME);
        String readyTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.READY_TIME);
        String questionTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.QUESTION_TIME);
        String totalNumKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TOTAL_NUM);

        valueOperations.set(phaseKey, 0);
        valueOperations.set(hostKey, debateCreateRequestDto.getHost());
        valueOperations.set(titleKey, debateCreateRequestDto.getTitle());
        valueOperations.set(keywordAKey, debateCreateRequestDto.getKeywordA());
        valueOperations.set(keywordBKey, debateCreateRequestDto.getKeywordB());
        valueOperations.set(isPrivateKey, debateCreateRequestDto.isPrivate());
        if(debateCreateRequestDto.isPrivate()) valueOperations.set(passwordKey, debateCreateRequestDto.getPassword());
        valueOperations.set(playerNumKey, debateCreateRequestDto.getPlayerNum());
        valueOperations.set(jurorNumKey, debateCreateRequestDto.getJurorNum());
        valueOperations.set(opinionTimeKey, debateCreateRequestDto.getOpinionTime());
        valueOperations.set(readyTimeKey, debateCreateRequestDto.getReadyTime());
        valueOperations.set(questionTimeKey, debateCreateRequestDto.getQuestionTime());
        valueOperations.set(totalNumKey, 1);

        return debateId;
    }

    @Transactional
    public int changeSetting(DebateChangeSettingRequestDto debateChangeSettingRequestDto) {
        int debateId = debateChangeSettingRequestDto.getDebateId();

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String titleKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TITLE);
        String keywordAKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_A);
        String keywordBKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.KEY_WORD_B);
        String isPrivateKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.IS_PRIVATE);
        String passwordKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.PASSWORD);
        String opinionTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.OPINION_TIME);
        String readyTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.READY_TIME);
        String questionTimeKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.QUESTION_TIME);

        valueOperations.set(titleKey, debateChangeSettingRequestDto.getTitle());
        valueOperations.set(keywordAKey, debateChangeSettingRequestDto.getKeywordA());
        valueOperations.set(keywordBKey, debateChangeSettingRequestDto.getKeywordB());
        valueOperations.set(isPrivateKey, debateChangeSettingRequestDto.isPrivate());
        if(debateChangeSettingRequestDto.isPrivate()) valueOperations.set(passwordKey, debateChangeSettingRequestDto.getPassword());
        valueOperations.set(opinionTimeKey, debateChangeSettingRequestDto.getOpinionTime());
        valueOperations.set(readyTimeKey, debateChangeSettingRequestDto.getReadyTime());
        valueOperations.set(questionTimeKey, debateChangeSettingRequestDto.getQuestionTime());

        return debateId;
    }

    @Transactional
    public void start(DebateStartRequestDto debateStartRequestDto) {
        int debateId = debateStartRequestDto.getDebateId();

        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();

        String viewerListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VIEWER_LIST);
        String teamAListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_A_LIST);
        String teamBListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.TEAM_B_LIST);
        String jurorListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.JUROR_LIST);

        List<Integer> teamAList = debateStartRequestDto.getTeamAList();
        for(Integer userId : teamAList) {
            stringObjectListOperations.rightPush(teamAListKey, userId);
        }
        List<Integer> teamBList = debateStartRequestDto.getTeamBList();
        for(Integer userId : teamBList) {
            stringObjectListOperations.rightPush(teamBListKey, userId);
        }
        List<Integer> jurorList = debateStartRequestDto.getJurorList();
        for(Integer userId : jurorList) {
            stringObjectListOperations.rightPush(jurorListKey, userId);
        }
        List<Integer> viewerList = debateStartRequestDto.getViewerList();
        for(Integer userId : viewerList) {
            stringObjectListOperations.rightPush(viewerListKey, userId);
        }
    }


    // 프론트에서 다 해
//    @Transactional
//    public int join(DebateJoinRequestDto debateJoinRequestDto) {
//        int debateId = debateJoinRequestDto.getDebateId();
//        int userId = debateJoinRequestDto.getUserId();
//
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
//
//        if(!debateRepository.existsById(debateId))
//            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
//
//        String viewerListKey = redisKeyUtil.getKeyByDebateIdWithKeyword(debateId, DebateRedisKey.VIEWER_LIST);
//
//        stringObjectListOperations.rightPush(viewerListKey, userId);
//
//        return debateId;
//    }

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
