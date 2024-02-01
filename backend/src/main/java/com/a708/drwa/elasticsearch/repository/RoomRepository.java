package com.a708.drwa.room.repository;

import com.a708.drwa.room.domain.Room;
import org.springframework.data.repository.CrudRepository;
import java.util.List;


public interface RoomRedisRepository extends CrudRepository<Room, String> {
    List<Room> findByName(String name);
}