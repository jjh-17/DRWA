package com.a708.drwa.record.domain.data.dto.request;

import com.a708.drwa.record.domain.Result;
import com.a708.drwa.record.domain.Team;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RecordRequestSaveDto {

    @NotNull
    private int memberId;

    @NotNull
    private int gameId;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private Result result;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private Team team;

    @Builder
    public RecordRequestSaveDto(int memberId, int gameId, Result result, Team team){
        this.memberId = memberId;
        this.gameId = gameId;
        this.result = result;
        this.team = team;
    }

    public 

}
