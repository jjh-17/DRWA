package com.a708.drwa.debate.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DebateMembers {
    private List<DebateMember> leftMembers;
    private List<DebateMember> rightMembers;
    private List<DebateMember> jurors;
    private List<DebateMember> watchers;
}
