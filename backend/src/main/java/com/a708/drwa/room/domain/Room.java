package com.a708.drwa.room.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "room")
public class Room {

    @Id
    private String title;

    private String keyword;

    private String host;

    //getters and setters

}


