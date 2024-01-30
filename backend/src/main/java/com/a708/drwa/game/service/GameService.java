package com.a708.drwa.game.service;

import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestDto;
import com.a708.drwa.game.data.dto.request.RecordCreateRequestDto;
import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.domain.Record;
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

    // 정산 시점에 몇명이 있든 단 한번만 실행되어야 한다.(프론트 단에서 Host만 수행하도록 하면 되나?)
    @Transactional
    public GameInfo createGameInfo(GameInfoCreateRequestDto gameInfoRequestDto) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(gameInfoRequestDto.getKeyword())
                .mvpMemberId(gameInfoRequestDto.getMvpMemberId())
                .build();

        return gameInfoRepository.save(gameInfo);
    }

    // 정산 시점에 모든 토론자와 배심원이 수행한다.햣
    @Transactional
    public Record createRecord(RecordCreateRequestDto recordRequestDto) {
        Record record = Record.builder()
                .memberId(recordRequestDto.getMemberId())
                .gameId(recordRequestDto.getGameId())
                .result(recordRequestDto.getResult())
                .team(recordRequestDto.getTeam())
                .build();

        return recordRepository.save(record);
    }
}
