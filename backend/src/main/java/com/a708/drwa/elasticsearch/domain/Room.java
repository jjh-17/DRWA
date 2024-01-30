package com.a708.drwa.elasticsearch.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "room")
//@Mapping(mappingPath = "elastic/room-mapping.json")
//@Setting(settingPath = "elastic/room-setting.json")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Field(type = FieldType.Keyword)
    private int id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String keyword;

    @Field(type = FieldType.Text)
    private String host;

    @Builder
    public Room(String title, String keyword, String host) {
        this.title = title;
        this.keyword = keyword;
        this.host = host;
    }
}


