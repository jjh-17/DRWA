package com.a708.drwa.elasticsearch.service;

import com.a708.drwa.elasticsearch.domain.Room;
import com.a708.drwa.elasticsearch.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public Room getRoomByTitle(String title){//제목 검색해서 검색어에 해당하는 방 가정오기
        Optional<Room> roomOptional = roomRepository.findByTitle(title);
        return roomOptional.orElse(null);
    }

    public Room getRoomByKeyword(String keyword){//키워드 검색해서 검색어에 해당하는 방 가져오기
        Optional<Room> roomOptional = roomRepository.findByKeyword(keyword);
        return roomOptional.orElse(null);
    }
}
