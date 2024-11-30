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
    public @ResponseBody List<Room> getRooms(@RequestParam(required = false) String areaId) {
        if (areaId != null && !areaId.isEmpty()) {
            log.info("Area ID {}", areaId);
            return roomRepository.findAllByAreaId(areaId);
        } else {
            return roomRepository.findAll();
        }
    }

    @DeleteMapping(path = "/api/v1/rooms")
    public @ResponseBody String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = roomRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" Room objects.");
        roomRepository.deleteAll();
        return response.toString();
    }

    @GetMapping(path = "/api/v1/room")
    public Room getRoomByVnum(@RequestParam String vnum) {
        return roomRepository.findRoomByVnum(vnum);
    }

    @GetMapping(path = "/api/v1/room/{id}")
    public Room getRoomByName(@PathVariable String areaId, @RequestParam String roomName) {
        log.info("Room {} with areaId {}", roomName, areaId);
        return roomRepository.findRoomByNameAndAreaId(roomName, areaId);
    }

    @PostMapping(path = "/api/v1/room")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomRepository.save(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }
}
