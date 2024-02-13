package com.a708.drwa.openvidu.data.dto.response;

import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import io.openvidu.java.client.Connection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetConnectionResponseDto {
    private Connection connection;
    private DebateInfoResponse debateInfoResponse;

    @Builder
    public GetConnectionResponseDto(Connection connection, DebateInfoResponse debateInfoResponse) {
        this.connection = connection;
        this.debateInfoResponse = debateInfoResponse;
    }
}
