package com.a708.drwa.debate.data.dto.response;

import com.a708.drwa.debate.data.DebateMember;
import com.a708.drwa.debate.data.DebateMembers;
import com.a708.drwa.debate.enums.DebateCategory;
import com.a708.drwa.debate.domain.DebateRoomInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DebateInfoResponse {
    private String sessionId;

    private DebateCategory debateCategory;

    private String hostName;

    private String title;

    private String leftKeyword;
    private String rightKeyword;

    private int playerNum;
    private int jurorNum;

    private List<DebateMember> teamAMembers;
    private List<DebateMember> teamBMembers;
    private List<DebateMember> jurors;
    private List<DebateMember> watchers;

    private Boolean isPrivate;
    private String password;

    private int speakingTime;
    private int readyTime;
    private int qnaTime;

    private String thumbnail1;
    private String thumbnail2;

    private int totalCnt;

    public void setLists(DebateMembers debateMembers) {
        this.teamAMembers = debateMembers.getTeamAMembers();
        this.teamBMembers = debateMembers.getTeamBMembers();
        this.jurors = debateMembers.getJurors();
        this.watchers = debateMembers.getWatchers();
    }
}
