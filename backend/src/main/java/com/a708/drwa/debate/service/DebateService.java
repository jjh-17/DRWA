package com.a708.drwa.debate.service;

import com.a708.drwa.debate.data.dto.request.DebateCreateRequestDto;
import com.a708.drwa.debate.repository.DebateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DebateService {
    private final DebateRepository debateRepository;

    public int create(DebateCreateRequestDto debateCreateRequestDto) {
        return debateRepository.save(debateCreateRequestDto.toEntity()).getDebateId();
    }
}
