package com.a708.drwa.room.domain;

import org.springframework.data.elasticsearch.annotations.Document;
import jakarta.persistence.Id;

@Document(indexName = "room")
public class Room {

    @Id
    private String title;

    private String keyword;

    private String host;

    //getters and setters

}


