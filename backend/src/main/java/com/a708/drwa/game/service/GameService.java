package com.a708.drwa.game.service;

import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestDto;
import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestRedisDto;
import com.a708.drwa.game.data.dto.request.RecordRequestDto;
import com.a708.drwa.game.data.dto.request.RecordRequestRedisDto;
import com.a708.drwa.game.data.dto.response.GameInfoCreateResponseDto;
import com.a708.drwa.game.data.dto.response.GameInfoCreateResponseRedisDto;
import com.a708.drwa.game.data.dto.response.RecordResponseDto;
import com.a708.drwa.game.data.dto.response.RecordResponseRedisDto;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.domain.Result;
import com.a708.drwa.game.domain.Team;
import com.a708.drwa.game.repository.GameInfoRepository;
import com.a708.drwa.game.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameInfoRepository gameInfoRepository;
    private final RecordRepository recordRepository;
    private final int pointWeight = 1;      // 1투표 당 포인트
    private final int pointMvpWeight = 5;   // MVP로 선정되었을 시 가산점

    // Host가 토론 시작을 누르면 수행됨
    @Transactional
    public GameInfoCreateResponseDto createGameInfo(GameInfoCreateRequestDto gameInfoCreateRequestDto) {
        GameInfo savedGameInfo = gameInfoRepository.save(GameInfo.builder()
                        .keyword(gameInfoCreateRequestDto.)
                .build());
    }

    // 전적 생성 => 모든 참여자
    @Transactional
    public RecordResponseDto createRecord(RecordRequestDto recordRequestDto) {
        // Redis 통신을 위한 Request DTO 초기화
        RecordRequestRedisDto recordRequestRedisDto = RecordRequestRedisDto
                .builder()
                .debateId(recordRequestDto.getDebateId())
                .build();

        // Redis 통신 결과
        RecordResponseRedisDto recordResponseRedisDto
                = recordRepository.getRecordResponseRedisDto(recordRequestRedisDto);

        // MySQL 통신
        Result result;
        Team team;
        if(recordResponseRedisDto.getTeamAList().)
        Record savedRecord = recordRepository.save(Record.builder()
                        .gameId(recordRequestDto.getGameId())
                        .memberId(recordRequestDto.getMemberId())
                        .result()
                        .team()
                .build());
        GameInfo savedGameInfo = gameInfoRepository.save(GameInfo.builder()
                .keyword(gameInfoCreateResponseRedisDto.getKeyword())
                .build());

        // 클라이언트에게 전달할 데이터 반환
        return GameInfoCreateResponseDto.builder()
                .gameId(savedGameInfo.getGameId())
                .build();
    }

    private int
}
