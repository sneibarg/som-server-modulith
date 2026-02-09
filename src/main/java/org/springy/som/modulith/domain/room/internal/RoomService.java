package org.springy.som.modulith.domain.room.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.room.api.RoomApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.roomIdMissing;
import static org.springy.som.modulith.domain.DomainGuards.roomMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class RoomService implements RoomApi {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRoomsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<RoomDocument> getAllRooms() {
        return roomRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RoomDocument getRoomByName(@RequestParam String name) {
        return roomRepository.findRoomById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RoomDocument getRoomById(@RequestParam String itemId) {
        return roomRepository.findRoomById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RoomDocument createRoom(@Valid @RequestBody RoomDocument roomDocument) {
        requireEntityWithId(roomDocument, RoomDocument::getId, roomMissing(), roomIdMissing());

        try {
            // if (roomRepository.existsById(roomDocument.getId())) throw new RoomConflictException(...)
            return roomRepository.save(roomDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createRoom roomId={}", safeId(roomDocument, RoomDocument::getId), ex);
            throw new RoomPersistenceException("Failed to create ROM roomDocument: "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RoomDocument saveRoomForId(String id, RoomDocument roomDocument) {
        requireText(id, roomIdMissing());
        requireEntityWithId(roomDocument, RoomDocument::getId, roomMissing(), roomIdMissing());

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

    private List<RoomDocument> getAllRoomsFallback(Throwable t) {
        log.warn("Fallback getAllRooms due to {}", t.toString());
        return List.of();
    }

    private RoomDocument getRoomByIdFallback(String id, Throwable t) {
        log.warn("Fallback getRoomById id={} due to {}", id, t.toString());
        throw new RoomPersistenceException("ROM room lookup temporarily unavailable: " + id+" "+t);
    }
}
