package com.a708.drwa.title.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TitleResponse {
    private String name;
    private String description;
}
