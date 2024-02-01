package com.a708.drwa.room.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import jakarta.persistence.Id;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;




@Document(indexName = "room_index") // Elasticsearch 문서
@Setting(settingPath = "resources/settings.json") // Elasticsearch 설정
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}


