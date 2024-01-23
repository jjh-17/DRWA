package com.a708.drwa.game.service;

import com.a708.drwa.game.data.dto.request.GameInfoSaveRequestDto;
import com.a708.drwa.game.data.dto.request.RecordSaveRequestDto;
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

    @Transactional
    public GameInfo createGameInfo(GameInfoSaveRequestDto gameInfoRequestDto) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(gameInfoRequestDto.getKeyword())
                .mvpMemberId(gameInfoRequestDto.getMvpMemberId())
                .build();
        return gameInfoRepository.save(gameInfo);
    }

    @Transactional
    public Record createRecord(RecordSaveRequestDto recordRequestDto) {
        Record record = Record.builder()
                .memberId(recordRequestDto.getMemberId())
                .gameId(recordRequestDto.getGameId())
                .result(recordRequestDto.getResult())
                .team(recordRequestDto.getTeam())
                .build();

        return recordRepository.save(record);
    }
}
