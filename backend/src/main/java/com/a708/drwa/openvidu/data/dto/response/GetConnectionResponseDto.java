package com.a708.drwa.openvidu.data.dto.response;

import io.openvidu.java.client.Connection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetConnectionResponseDto {
    private Connection connection;

    @Builder
    public GetConnectionResponseDto(Connection connection) {
        this.connection = connection;
    }
}
