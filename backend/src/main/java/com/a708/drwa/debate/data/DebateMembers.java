package com.a708.drwa.debate.data;

import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class DebateMembers {
    private List<DebateMember> leftMembers;
    private List<DebateMember> rightMembers;
    private List<DebateMember> jurors;
    private List<DebateMember> watchers;
}
