package com.a708.drwa.debate.data;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class VoteInfo {
    private int leftVote;
    private int rightVote;

}
