package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.room.Room;
import org.springy.som.modulith.repository.rom.RoomRepository;

import java.util.List;

@Slf4j
@RestController
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping(path = "/api/v1/rooms")
    @ResponseBody
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @GetMapping(path = "/api/v1/room")
    public Room getRoomByVnum(@RequestParam String vnum) {
        return roomRepository.findRoomByVnum(vnum);
    }

    @PostMapping(path = "/api/v1/room")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomRepository.save(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/api/v1/rooms")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = roomRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" Room objects.");
        roomRepository.deleteAll();
        return response.toString();
    }
}
