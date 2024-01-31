package com.a708.drwa.debate.data.dto;

import com.a708.drwa.debate.domain.enums.Role;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DebateMemberDto {
    private int memberId;
    private Role role;
}
