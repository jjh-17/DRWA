package com.a708.drwa.gameinfo.service;

import com.a708.drwa.gameinfo.domain.GameInfo;
import com.a708.drwa.gameinfo.data.dto.request.GameInfoSaveRequestDto;
import com.a708.drwa.gameinfo.repository.GameInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GameInfoService {

    private final GameInfoRepository gameInfoRepository;

    @Transactional
    public GameInfo createGameInfo(GameInfoSaveRequestDto gameInfoRequestDto) {
        GameInfo gameInfo = GameInfo.builder()
                .keyword(gameInfoRequestDto.getKeyword())
                .mvpMemberId(gameInfoRequestDto.getMvpMemberId())
                .build();
        return gameInfoRepository.save(gameInfo);
    }

}
