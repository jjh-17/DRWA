package com.a708.drwa.record.service;

import com.a708.drwa.record.domain.Record;
import com.a708.drwa.record.data.dto.request.RecordSaveRequestDto;
import com.a708.drwa.record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;

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
