package org.springy.som.modulith.domain.room.api;

import org.springy.som.modulith.domain.room.internal.RoomDocument;

import java.util.List;

public interface RoomApi {
    List<RoomDocument> getAllRooms();
    RoomDocument getRoomByName(String name);
    RoomDocument getRoomById(String id);
    RoomDocument createRoom(RoomDocument roomDocument);
    RoomDocument saveRoomForId(String id, RoomDocument roomDocument);
    void deleteRoomById(String id);
    long deleteAllRooms();
}
