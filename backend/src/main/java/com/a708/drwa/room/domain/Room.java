package com.a708.drwa.room.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@Document(indexName = "room_index")
public class Room { // Elasticsearch에 저장되는 방 정보
    @Id
    private String sessionId;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String title;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String leftKeyword;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String rightKeyword;

    private String hostName;

    private String thumbnail1;

    private String thumbnail2;

    private String debateCategory;

    private Integer playerNum;

    private Integer jurorNum;

    private Boolean isPrivate;

    private String password;

    private Integer speakingTime;

    private Integer readyTime;

    private Integer qnaTime;;

    private Integer totalCnt;

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
