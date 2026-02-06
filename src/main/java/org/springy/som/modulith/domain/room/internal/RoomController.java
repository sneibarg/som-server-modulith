package org.springy.som.modulith.domain.room.internal;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.web.DeleteAllResponse;
import org.springy.som.modulith.domain.room.api.RoomMapper;
import org.springy.som.modulith.domain.room.api.RoomView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/rooms", produces = "application/json")
public class RoomController {
    private final RoomService roomService;

    public  RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<RoomView>> getRooms() {
        List<RoomView> roomViews = roomService.getAllRooms()
                .stream()
                .map(RoomMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roomViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RoomView> getRoomById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(RoomMapper.toView(roomService.getRoomById(id)));
    }

    @PostMapping
    public ResponseEntity<RoomView> createRoom(@Valid @RequestBody RoomDocument roomDocument) {
        RoomDocument saved = roomService.createRoom(roomDocument);
        RoomView roomView = RoomMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/races/" + saved.getId()))
                .body(roomView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomView> updateRoom(@PathVariable String id, @Valid @RequestBody RoomDocument roomDocument) {
        RoomDocument updated = roomService.saveRoomForId(id, roomDocument);
        RoomView roomView = RoomMapper.toView(updated);
        return ResponseEntity.ok(roomView);
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
