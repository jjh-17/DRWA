package com.a708.drwa.room.controller;

import com.a708.drwa.room.data.dto.RoomResponseDto;
import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.service.RoomSearchService;
import com.a708.drwa.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomSearchService roomSearchService;
    private final RoomService roomService;

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
    @GetMapping("/popular")
    public ResponseEntity<List<RoomResponseDto>> getPopularRooms() {
        List<RoomResponseDto> popularRooms = roomService.getPopularRooms();
        return ResponseEntity.ok(popularRooms);
    }

    @PostMapping("/rooms/thumbnail")
    public ResponseEntity<?> updateRoomThumbnail(@RequestBody Room.ThumbnailUpdateInfo thumbnailUpdateInfo) {
        Room room = roomService.findRoomById(thumbnailUpdateInfo.getRoomId());
        if (room == null) {
            return ResponseEntity.notFound().build();
        }

        room.updateThumbnail(thumbnailUpdateInfo);
        roomService.saveRoomInRedis(room);

        return ResponseEntity.ok().build();
    }
}