package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.exception.RedisErrorCode;
import com.a708.drwa.redis.exception.RedisException;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final Map<String, ScheduledFuture<?>> scheduledFutures;

    /**
     * 토론 방 생성
     * @param debateCreateRequestDto
     * @return
     */
    @Transactional
    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        // 방 저장 및 아이디 추출
        return debateRepository.save(Debate.builder()
                        .debateCategory(debateCreateRequestDto.getDebateCategory())
                        .build())
                .getDebateId();
    }

    /**
     * 토론 시작
     * 방 설정 정보, 참여자 리스트 저장
     * @param debateStartRequestDto
     */
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
        for(Map.Entry<Integer, DebateMember> member : debateStartRequestDto.getMemberDtoHashMap().entrySet()) {
            DebateMember memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        // 준비 단계로 PHASE 진행
        nextPhase(debateKey);
    }

    public void nextPhase(String debateKey) {
        // redis 객체
        HashOperations<String, DebateRedisKey, Object> hash = redisTemplate.opsForHash();

        // phase ++
        hash.increment(debateKey, DebateRedisKey.PHASE, 1);
        Integer phase = (Integer) hash.get(debateKey, DebateRedisKey.PHASE);
        if(phase == null)
            throw new RedisException(RedisErrorCode.INVALID_KEY_ERROR);

        // phase 값으로 무슨 단계인지 확인
        // -2 : 대기 단계
        // (phase % 4) % 2 == 0  : phase / 4 번째 사람 발언 단계. (phase % 4) % 2가 0이면 Left, 1이면 Right
        // phase / 4 == playerNum / 2 : 투표 집계 단계. 결과 반영 후 대기 단계(-2)로 돌아가기

        // -1 : 준비 단계
        if(phase < 0) {
            readyPhase(debateKey);
        } else {
            // 단계 앞자리 '1'-1
            int phaseRow = phase / 4;
            // 단계 뒷자리 1-'1'
            int phaseCol = phase % 4;

            // 발언 1 > 질의 1 > 발언 2 > 질의 2 참여자 수 만큼 사이클 반복
            switch (phaseCol) {
                case 0:
                    speakPhase(debateKey, phaseRow, DebateRedisKey.TEAM_A);
                case 2:
                    speakPhase(debateKey, phaseRow, DebateRedisKey.TEAM_B);
                case 1:
                case 3:
                    qnaPhase(debateKey);

            }
        }

        // 값에 따라 처리 다르게
        // 발언 이었으면 질의, 질의 였으면 발언, 마지막 질의면 투표 집계 후 대기로 돌아가기

        // 단계 별 설정된 시간 후에 nextPhase 호출하게 Scheduler? 사용

        // 단계에 따라 마이크 권한 줄 유저 아이디 반환해주자.
    }

    public void readyPhase(String debateKey) {
        HashOperations<String, DebateRedisKey, Object> hash = redisTemplate.opsForHash();

        // 방 정보 가져오기
        RoomInfo roomInfo = (RoomInfo) hash.get(debateKey, DebateRedisKey.ROOM_INFO);

        // 참여자 리스트
        DebateMembers debateMembers = (DebateMembers) hash.get(debateKey, DebateRedisKey.DEBATE_MEMBER_LIST);

        ///////////////////// Redis PubSub /////////////////////
        // 채널 분리

        ////////////////////////////////////////////////////////

        // scheduler 예약
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> future = executorService.schedule(new Runnable() {
            @Override
            public void run() {
                nextPhase(debateKey);
            }
        }, roomInfo.getReadyTime(), TimeUnit.SECONDS);

        scheduledFutures.put(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, "readyPhase"), future);
    }

    public void speakPhase(String key, int phaseRow, DebateRedisKey HKey) {
        HashOperations<String, DebateRedisKey, Object> hash = redisTemplate.opsForHash();

        // 참여자 리스트 요청
        DebateMembers debateMembers = (DebateMembers) hash.get(key, DebateRedisKey.DEBATE_MEMBER_LIST);

        // 발언자 결정
        if(HKey)
        DebateMember currentSpeakingMember = debateMembers.
        String currentSpeakingUserNickname = "";
        String currentSpeakingTeam = "";

        if (currentPhaseDetail == 1) {
            currentSpeakingTeam = "LEFT";
            currentSpeakingUserNickname = leftUserList.get(currentPhase - 1);
        } else if (currentPhaseDetail == 2) {
            currentSpeakingTeam = "RIGHT";
            currentSpeakingUserNickname = rightUserList.get(currentPhase - 1);
        }


        String currentSpeakingUserKey = redisKeyUtil.currentSpeakingUserKey(roomId);
        String currentSpeakingTeamKey = redisKeyUtil.currentSpeakingTeamKey(roomId);

        redisTemplate.opsForValue().set(currentSpeakingUserKey, currentSpeakingUserNickname);
        redisTemplate.opsForValue().set(currentSpeakingTeamKey, currentSpeakingTeam);

        String roomChannelKey = redisChannelUtil.roomChannelKey(roomId);

        String phaseStartAllInOneMessage = redisMessageUtil.speakPhaseStartMessage(currentPhase, currentPhaseDetail, currentSpeakingTeam, currentSpeakingUserNickname);
        redisPublisher.publishMessage(roomChannelKey, phaseStartAllInOneMessage);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

        ScheduledFuture<?> future = executorService.schedule(new Runnable() {
            @Override
            public void run() {

                redisTemplate.opsForValue().set(currentSpeakingTeamKey, "");
                redisTemplate.opsForValue().set(currentSpeakingUserKey, "");

                nextPhase(roomId);
            }
        }, 180, TimeUnit.SECONDS);
        // 테스트용 10초 실제 서비스 180초
        scheduledFutures.put(roomId + "_speakingPhase", future);
    }


    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }
}
