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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String debateId;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String title;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String keywordA;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String keywordB;

    private String host;

    private String totalNum;

    private String thumbnailAUrl;

    private String thumbnailBUrl;

    private String thumbnailAId;

    private String thumbnailBId;

    @Getter
    @Setter
    public static class ThumbnailUpdateInfo {
        private String roomId;
        private String thumbnailId; // 썸네일A 또는 썸네일B의 ID
        private String thumbnailUrl; // 새로운 썸네일 URL
        private String thumbnailType; // 'A' 또는 'B'
    }

    public void updateThumbnail(ThumbnailUpdateInfo thumbnailUpdateInfo) {
        if ("A".equals(thumbnailUpdateInfo.getThumbnailType())) {
            this.thumbnailAId = thumbnailUpdateInfo.getThumbnailId();
            this.thumbnailAUrl = thumbnailUpdateInfo.getThumbnailUrl();
        } else if ("B".equals(thumbnailUpdateInfo.getThumbnailType())) {
            this.thumbnailBId = thumbnailUpdateInfo.getThumbnailId();
            this.thumbnailBUrl = thumbnailUpdateInfo.getThumbnailUrl();
        }
    }

}
