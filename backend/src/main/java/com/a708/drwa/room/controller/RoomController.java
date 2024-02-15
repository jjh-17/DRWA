package com.a708.drwa.room.controller;

import com.a708.drwa.debate.data.dto.response.DebateInfoListResponse;
import com.a708.drwa.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/searchAll")
    public ResponseEntity<DebateInfoListResponse> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<DebateInfoListResponse> findByCondition(@RequestParam String condition,
                                                         @RequestParam String value,
                                                         @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(roomService.findByCondition(condition, value, pageable));
    }
}