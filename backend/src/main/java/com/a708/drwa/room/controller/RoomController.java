package com.a708.drwa.room.controller;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.service.RoomSearchService;
import com.a708.drwa.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomSearchService roomSearchService;
    private final RoomService roomService;

    @GetMapping("/search/title")
    public ResponseEntity<List<Room>> searchRoomsByTitle(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRooms("title", query); // Elasticsearch에서 제목으로 방 검색
        List<Room> fullRoomDetails = rooms.stream()
                .map(room -> roomService.findRoomById(room.getSessionId())) // 각 Room 객체의 debateId를 사용하여 Redis에서 방 정보 조회
                .collect(Collectors.toList());
        return ResponseEntity.ok(fullRoomDetails);
    }


    @GetMapping("/search/keyword")
    public ResponseEntity<List<Room>> searchRoomsByKeyword(@RequestParam String query) {
        List<Room> rooms = roomSearchService.searchRooms("keyword", query); // Elasticsearch에서 제목으로 방 검색
        List<Room> fullRoomDetails = rooms.stream()
                .map(room -> roomService.findRoomById(room.getSessionId())) // 각 Room 객체의 debateId를 사용하여 Redis에서 방 정보 조회
                .collect(Collectors.toList());
        return ResponseEntity.ok(fullRoomDetails);
    }

//    @PostMapping("/rooms/thumbnail")
//    public ResponseEntity<?> updateRoomThumbnail(@RequestBody Room.ThumbnailUpdateInfo thumbnailUpdateInfo) {
//        Room room = roomService.findRoomById(thumbnailUpdateInfo.getRoomId());
//        if (room == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        room.updateThumbnail(thumbnailUpdateInfo);
//        roomService.saveRoomInRedis(room);
//
//        return ResponseEntity.ok().build();
//    }

    @PostMapping("/rooms/thumbnail")
    public ResponseEntity<?> updateRoomThumbnail(@RequestBody Room.ThumbnailUpdateInfo thumbnailUpdateInfo) {
        roomService.updateRoomThumbnail(thumbnailUpdateInfo);
        return ResponseEntity.ok().build(); // 성공 응답
    }
}