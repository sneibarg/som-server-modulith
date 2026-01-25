package org.springy.som.modulith.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.service.RoomService;

import java.util.List;

@Slf4j
@RestController
public class RoomController {
    private final RoomService roomService;

    public  RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping(path = "/api/v1/rooms")
    public @ResponseBody List<Room> getRooms(@RequestParam(required = false) String areaId) {
        if (areaId != null && !areaId.isEmpty()) {
            log.info("Area ID {}", areaId);
            return roomService.getRoomsByAreaId(areaId);
        } else {
            return roomService.getAllRooms();
        }
    }

    @DeleteMapping(path = "/api/v1/rooms")
    public @ResponseBody String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(roomService.deleteAllRooms())
                .append(" Room objects.");
        return response.toString();
    }

    @GetMapping(path = "/api/v1/room")
    public Room getRoomByVnum(@RequestParam String vnum) {
        return roomService.getRoomByVnum(vnum);
    }

    @GetMapping(path = "/api/v1/room/{id}")
    public Room getRoomByName(@PathVariable String areaId, @RequestParam String roomName) {
        log.info("Room {} with areaId {}", roomName, areaId);
        return roomService.getRoomByNameAndAreaId(roomName, areaId);
    }

    @PostMapping(path = "/api/v1/room")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.saveRoom(room);
        return new ResponseEntity<>(savedRoom, HttpStatus.CREATED);
    }
}
