package com.a708.drwa.room.controller;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.service.RoomSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    private RoomSearchService roomSearchService;

    // Elasticsearch 기본 검색
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRooms(query);
        return ResponseEntity.ok(rooms);
    }

    // Nori 분석기를 사용한 검색
    @GetMapping("/nori")
    public ResponseEntity<List<Room>> searchRoomsByNori(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRoomsByNori(query);
        return ResponseEntity.ok(rooms);
    }


}
