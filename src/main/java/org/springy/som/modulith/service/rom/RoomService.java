package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.room.RomRoom;
import org.springy.som.modulith.repository.RoomRepository;

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
    public List<RomRoom> getRooms() {
        return roomRepository.findAll();
    }

    @GetMapping(path = "/api/v1/room")
    public RomRoom getRoomByVnum(@RequestParam String vnum) {
        return roomRepository.findRoomByVnum(vnum);
    }

    @PostMapping(path = "/api/v1/room")
    public ResponseEntity<RomRoom> createRoom(@RequestBody RomRoom romRoom) {
        RomRoom savedRomRoom = roomRepository.save(romRoom);
        return new ResponseEntity<>(savedRomRoom, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/api/v1/rooms")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = roomRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" RomRoom objects.");
        roomRepository.deleteAll();
        return response.toString();
    }
}
