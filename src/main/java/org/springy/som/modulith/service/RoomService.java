package org.springy.som.modulith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.repository.RoomRepository;

import java.util.List;

@Slf4j
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    public List<Room> getRoomsByAreaId(@RequestParam String areaId) {
        return roomRepository.findAllByAreaId(areaId);
    }

    public Room getRoomById(@RequestParam String id) {
        return roomRepository.findRoomById(id);
    }

    public Room getRoomByVnum(@RequestParam String vnum) {
        return roomRepository.findRoomByVnum(vnum);
    }

    public Room getRoomByNameAndAreaId(@RequestParam String roomName, @RequestParam String areaId) {
        return roomRepository.findRoomByNameAndAreaId(roomName, areaId);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public long deleteAllRooms() {
        long roomCount = roomRepository.count();
        roomRepository.deleteAll();
        return roomCount;
    }
}
