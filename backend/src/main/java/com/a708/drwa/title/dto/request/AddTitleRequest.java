package com.a708.drwa.title.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AddTitleRequest {
    private String name;
    private String description;
}
