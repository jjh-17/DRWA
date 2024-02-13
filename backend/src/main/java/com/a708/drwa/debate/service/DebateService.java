package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.data.RoomInfo;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.data.dto.response.DebateInfoListResponse;
import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.debate.repository.DebateRoomRepository;
import com.a708.drwa.debate.scheduler.RoomsKey;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.exception.RedisErrorCode;
import com.a708.drwa.redis.exception.RedisException;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DebateService {
    private final DebateRepository debateRepository;
    private final DebateRoomRepository debateRoomRepository;
    private final RedisTemplate<String, DebateInfoResponse> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;
    private final Map<String, ScheduledFuture<?>> scheduledFutures;

    /**
     * 방 목록 전체 조회
     * @return DebateInfoListResponse
     */
    public DebateInfoListResponse findAll() {
        List<DebateInfoResponse> debateInfoResponses = new ArrayList<>();
        for (DebateRoomInfo debateRoomInfo : debateRoomRepository.findAll()) {
            debateInfoResponses.add(debateRoomInfo.toResponse());
        }
        return DebateInfoListResponse.builder()
                .debateInfoResponses(debateInfoResponses)
                .build();
    }

    /**
     * 인기순 5개 가져오기
     * @return DebateInfoListResponse
     */
    public DebateInfoListResponse getTop5Debates() {
        // 인기순 5개 sessionId 검색
        ZSetOperations<String, DebateInfoResponse> zSet = redisTemplate.opsForZSet();
        return DebateInfoListResponse.builder()
                .debateInfoResponses(zSet.reverseRange(RoomsKey.ROOM_POPULAR_KEY, 0, -1).stream().toList())
                .build();
    }

//    public DebateInfoListResponse getDebatesByCategory(String category) {
//
//    }

    /**
     * 토론 시작
     * 방 설정 정보, 참여자 리스트 저장
     * @param debateStartRequestDto
     */
    public void start(DebateStartRequestDto debateStartRequestDto) {
//        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
//        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
//
//        String debateKey = debateStartRequestDto.getDebateId() + "";
//        String startTimeKey = DebateRedisKey.START_TIME.string();
//        String roomInfoKey = DebateRedisKey.ROOM_INFO.string();
//
//        // 시작 시간 및 설정 저장
//        hashOperations.put(debateKey, startTimeKey, System.currentTimeMillis() / 1000L + "");
//        hashOperations.put(debateKey, roomInfoKey, debateStartRequestDto.getRoomInfo());
//
//        // 유저 리스트 저장
//        for(Map.Entry<Integer, DebateMember> member : debateStartRequestDto.getMemberDtoHashMap().entrySet()) {
//            DebateMember memberDto = member.getValue();
//            String teamKey = member.getValue().getRole().string();
//            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
//        }
//
//        // Redis에 방 정보 저장을 위한 saveDebateRoomInfo 호출
//        try {
//            saveDebateRoomInfo(debateStartRequestDto);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//            // JSON 변환 실패 처리 로직 (예외 로깅, 사용자에게 오류 응답 등)
//        }
//
//        // 준비 단계로 PHASE 진행
//        nextPhase(debateKey);
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
        // phase / 4 == playerNum : 투표 집계 단계. 결과 반영 후 대기 단계(-2)로 돌아가기

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
                    break;
                case 2:
                    speakPhase(debateKey, phaseRow, DebateRedisKey.TEAM_B);
                    break;
                case 1:
                case 3:
                    qnaPhase(debateKey);
                    break;
                default:
                    throw new DebateException(DebateErrorCode.INTERNAL_ERROR);
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
//        if(HKey)
//        DebateMember currentSpeakingMember = debateMembers.
//        String currentSpeakingUserNickname = "";
//        String currentSpeakingTeam = "";
//
//        if (currentPhaseDetail == 1) {
//            currentSpeakingTeam = "LEFT";
//            currentSpeakingUserNickname = leftUserList.get(currentPhase - 1);
//        } else if (currentPhaseDetail == 2) {
//            currentSpeakingTeam = "RIGHT";
//            currentSpeakingUserNickname = rightUserList.get(currentPhase - 1);
//        }
//
//
//        String currentSpeakingUserKey = redisKeyUtil.currentSpeakingUserKey(roomId);
//        String currentSpeakingTeamKey = redisKeyUtil.currentSpeakingTeamKey(roomId);
//
//        redisTemplate.opsForValue().set(currentSpeakingUserKey, currentSpeakingUserNickname);
//        redisTemplate.opsForValue().set(currentSpeakingTeamKey, currentSpeakingTeam);
//
//        String roomChannelKey = redisChannelUtil.roomChannelKey(roomId);
//
//        String phaseStartAllInOneMessage = redisMessageUtil.speakPhaseStartMessage(currentPhase, currentPhaseDetail, currentSpeakingTeam, currentSpeakingUserNickname);
//        redisPublisher.publishMessage(roomChannelKey, phaseStartAllInOneMessage);
//
//        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
//
//        ScheduledFuture<?> future = executorService.schedule(new Runnable() {
//            @Override
//            public void run() {
//
//                redisTemplate.opsForValue().set(currentSpeakingTeamKey, "");
//                redisTemplate.opsForValue().set(currentSpeakingUserKey, "");
//
//                nextPhase(roomId);
//            }
//        }, 180, TimeUnit.SECONDS);
//        // 테스트용 10초 실제 서비스 180초
//        scheduledFutures.put(roomId + "_speakingPhase", future);
    }

    private void qnaPhase(String debateKey) {

    }

    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }

//    @Autowired
//    private ObjectMapper objectMapper; // Jackson ObjectMapper
//
//    public void saveDebateRoomInfo(DebateStartRequestDto debateStartRequestDto) throws JsonProcessingException {
//        RoomInfo roomInfo = debateStartRequestDto.getRoomInfo();
//        String roomJson = objectMapper.writeValueAsString(roomInfo); // RoomInfo 객체를 JSON 문자열로 변환
//        redisTemplate.opsForList().rightPush("room_updates", roomJson); // JSON 문자열을 Object로 캐스팅하여 저장
//    }
}
