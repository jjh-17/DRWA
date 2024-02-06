package com.a708.drwa.room.controller;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.service.RoomSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomSearchService roomSearchService;

    @GetMapping("/search/title")
    public ResponseEntity<List<Room>> searchRoomsByTitle(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRooms("title", query);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<Room>> searchRoomsByKeyword(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRooms("keyword", query);
        return ResponseEntity.ok(rooms);
    }
}