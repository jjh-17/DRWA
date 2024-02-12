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

    private String thumbnailUrl;

    private String thumbnailId;

    @Getter
    @Setter
    public static class ThumbnailUpdateInfo {
        private String roomId;
        private String thumbnailId;
        private String thumbnailUrl;
    }

    public void updateThumbnail(ThumbnailUpdateInfo thumbnailUpdateInfo) {
        this.thumbnailId = thumbnailUpdateInfo.getThumbnailId();
        this.thumbnailUrl = thumbnailUpdateInfo.getThumbnailUrl();
    }

}
