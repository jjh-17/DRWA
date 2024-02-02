package com.a708.drwa.room.controller;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.service.RoomSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RoomController {

    private final RoomSearchService roomSearchService;

    @Autowired
    public RoomController(RoomSearchService roomSearchService) {
        this.roomSearchService = roomSearchService;
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Room>> RoomsByTitle(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRoomsByTitle(query);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<Room>> RoomsByKeyword(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRoomsByKeyword(query);
        return ResponseEntity.ok(rooms);
    }

}