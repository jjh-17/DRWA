package com.a708.drwa.game.service;

import com.a708.drwa.game.domain.GameInfo;
import com.a708.drwa.game.data.dto.request.GameInfoCreateRequestDto;
import com.a708.drwa.game.repository.GameInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameInfoService {

    private final GameInfoRepository gameInfoRepository;

    @Transactional
    public GameInfo createGameInfo(GameInfoCreateRequestDto gameInfoRequestDto) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(gameInfoRequestDto.getKeyword())
                .mvpMemberId(gameInfoRequestDto.getMvpMemberId())
                .build();
        return gameInfoRepository.save(gameInfo);
    }

}
