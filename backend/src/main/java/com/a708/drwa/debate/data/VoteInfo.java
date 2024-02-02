package com.a708.drwa.debate.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VoteInfo {
    private int leftVote;
    private int rightVote;

}
