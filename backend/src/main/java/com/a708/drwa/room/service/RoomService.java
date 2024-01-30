package com.a708.drwa.room.service;

import com.a708.drwa.room.domain.Room;
import com.a708.drwa.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public Room getRoomByTitle(String title){//제목 검색해서 검색어에 해당하는 방 가정오기
        Optional<Room> roomOptional = roomRepository.findByTitle(title);
        return roomOptional.orElse(null);
    }

    public Room getRoomByKeyword(String keyword){//키워드 검색해서 검색어에 해당하는 방 가져오기
        Optional<Room> roomOptional = roomRepository.findByKeyword(keyword);
        return roomOptional.orElse(null);
    }
}
