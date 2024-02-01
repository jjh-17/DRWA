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


    // /search 엔드포인트 정의, 들어온 검색어에 따라 방 검색
    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRoomsByNori(query);
        return ResponseEntity.ok(rooms);
    }



}