package com.a708.drwa.room.domain;

import com.a708.drwa.debate.data.dto.response.DebateInfoResponse;
import com.a708.drwa.debate.enums.DebateCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setting(settingPath = "/elasticsearch/settings.json")
@Mapping(mappingPath = "/elasticsearch/mappings.json")
@Document(indexName = "room_index")
public class Room { // Elasticsearch에 저장되는 방 정보
    @Id
    private String sessionId;

    private DebateCategory debateCategory;

    private String hostName;

    private String title;

    private String leftKeyword;
    private String rightKeyword;

    private int playerNum;
    private int jurorNum;

    private Boolean isPrivate;
    private String password;

    private int speakingTime;
    private int readyTime;
    private int qnaTime;

    private String thumbnail1;
    private String thumbnail2;

    private int totalCnt;

    public DebateInfoResponse toDto() {
        return DebateInfoResponse.builder()
                .sessionId(this.sessionId)
                .debateCategory(this.debateCategory)
                .hostName(this.hostName)
                .title(this.title)
                .leftKeyword(this.leftKeyword)
                .rightKeyword(this.rightKeyword)
                .playerNum(this.playerNum)
                .jurorNum(this.jurorNum)
                .isPrivate(this.isPrivate)
                .password(this.password)
                .speakingTime(this.speakingTime)
                .readyTime(this.readyTime)
                .qnaTime(this.qnaTime)
                .thumbnail1(this.thumbnail1)
                .thumbnail2(this.thumbnail2)
                .totalCnt(this.totalCnt)
                .build();
    }

    public void join() {
        this.totalCnt++;
    }

    public void exit() {
        this.totalCnt--;
    }

    @Getter
    @Setter
    public static class ThumbnailUpdateInfo {
        private String sessionId;
        private String thumbnailUrl; // 새로운 썸네일 URL
        private String thumbnailType; // '1' 또는 '2'
    }

    public void updateThumbnail(ThumbnailUpdateInfo thumbnailUpdateInfo) {
        if ("1".equals(thumbnailUpdateInfo.getThumbnailType())) {
            this.thumbnail1 = thumbnailUpdateInfo.getThumbnailUrl();
        } else if ("2".equals(thumbnailUpdateInfo.getThumbnailType())) {
            this.thumbnail2 = thumbnailUpdateInfo.getThumbnailUrl();
        }
    }
}
