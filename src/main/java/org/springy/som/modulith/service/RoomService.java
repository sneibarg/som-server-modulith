package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.exception.room.RoomNotFoundException;
import org.springy.som.modulith.exception.room.RoomPersistenceException;
import org.springy.som.modulith.repository.RoomRepository;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.roomIdMissing;
import static org.springy.som.modulith.util.DomainGuards.roomMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRoomsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Room getRoomByName(@RequestParam String name) {
        return roomRepository.findRoomById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Room getRoomById(@RequestParam String itemId) {
        return roomRepository.findRoomById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Room createRoom(@Valid @RequestBody Room room) {
        requireEntityWithId(room, Room::getId, roomMissing(), roomIdMissing());

        try {
            // if (roomRepository.existsById(reset.getRoomById())) throw new ResetConflictException(...)
            return roomRepository.save(room);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createRoom areaId={}", safeId(room, Room::getId), ex);
            throw new RoomPersistenceException("Failed to create ROM race"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Room saveRoomForId(String id, Room room) {
        requireText(id, roomIdMissing());
        requireEntityWithId(room, Room::getId, roomMissing(), roomIdMissing());

        return roomRepository.save(getRoomById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteRoomById(String id) {
        requireText(id, roomIdMissing());

        try {
            if (!roomRepository.existsById(id)) {
                throw new RoomNotFoundException(id);
            }
            roomRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteRoomById id={}", id, ex);
            throw new RoomPersistenceException("Failed to delete ROM reset: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllRooms() {
        try {
            long itemCount = roomRepository.count();
            roomRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllRooms", ex);
            throw new RoomPersistenceException("Failed to delete all ROM rooms "+ ex);
        }
    }

    private List<Room> getAllRoomFallback(Throwable t) {
        log.warn("Fallback getAllRooms due to {}", t.toString());
        return List.of();
    }

    private Room getRoomsByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllRoomsById id={} due to {}", id, t.toString());
        throw new RoomPersistenceException("ROM room lookup temporarily unavailable: " + id+" "+t);
    }
}
