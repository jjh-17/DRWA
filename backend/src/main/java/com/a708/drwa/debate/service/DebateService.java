package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.DebateMemberDto;
import com.a708.drwa.debate.data.dto.RoomInfo;
import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateJoinRequestDto;
import com.a708.drwa.debate.data.dto.request.DebateStartRequestDto;
import com.a708.drwa.debate.domain.Debate;
import com.a708.drwa.debate.exception.DebateErrorCode;
import com.a708.drwa.debate.exception.DebateException;
import com.a708.drwa.debate.repository.DebateRepository;
import com.a708.drwa.redis.domain.DebateRedisKey;
import com.a708.drwa.redis.util.RedisKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisKeyUtil redisKeyUtil;

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
        for(Map.Entry<Integer, DebateMemberDto> member : debateStartRequestDto.getMemberDtoHashMap().entrySet()) {
            DebateMemberDto memberDto = member.getValue();
            String teamKey = member.getValue().getRole().string();
            listOperations.rightPush(redisKeyUtil.getKeyByRoomIdWithKeyword(debateKey, teamKey), memberDto);
        }

        // 준비 단계로 PHASE 진행
        hashOperations.increment(debateKey, DebateRedisKey.PHASE.string(), 1);
    }

    public void nextPhase(int debateId) {
        // redis 객체
        HashOperations<String, DebateRedisKey, Object> hash = redisTemplate.opsForHash();
        String debateKey = debateId + "";

        // 레디스에서 해당 방 정보 가져오기
        RoomInfo roomInfo = (RoomInfo) hash.get(debateKey, DebateRedisKey.ROOM_INFO);

        // phase ++
        hash.increment(debateKey, DebateRedisKey.PHASE, 1);
        int phase = (int) hash.get(debateKey, DebateRedisKey.PHASE);

        // phase 값으로 무슨 단계인지 확인
        // -2 : 대기 단계
        // -1 : 준비 단계
        // (phase % 4) % 2 == 0  : phase / 4 번째 사람 발언 단계. (phase % 4) % 2가 0이면 Left, 1이면 Right
        // phase / 4 == playerNum / 2 : 투표 집계 단계. 결과 반영 후 대기 단계(-2)로 돌아가기

        // 값에 따라 처리 다르게
        // 발언 이었으면 질의, 질의 였으면 발언, 마지막 질의면 투표 집계 후 대기로 돌아가기

        // 단계 별 설정된 시간 후에 nextPhase 호출하게 Scheduler? 사용

        // 단계에 따라 마이크 권한 줄 유저 아이디 반환해주자.
    }


    public Boolean isExistDebate(DebateJoinRequestDto debateJoinRequestDto) {
        // 존재하지 않는 방일 경우
        if(!debateRepository.existsById(debateJoinRequestDto.getDebateId()))
            throw new DebateException(DebateErrorCode.NOT_EXIST_DEBATE_ROOM_ERROR);
        return true;
    }
}
