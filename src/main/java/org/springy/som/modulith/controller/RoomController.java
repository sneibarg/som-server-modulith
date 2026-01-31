package org.springy.som.modulith.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.RoomService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/rooms", produces = "application/json")
public class RoomController {
    private final RoomService roomService;

    public  RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Room> getRoomById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room saved = roomService.createRoom(room);
        return ResponseEntity
                .created(URI.create("/api/v1/races/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable String id, @Valid @RequestBody Room room) {
        Room updated = roomService.saveRoomForId(id, room);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable String id) {
        roomService.deleteRoomById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(roomService.deleteAllRooms()));
    }
}
