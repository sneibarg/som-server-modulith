package org.springy.som.modulith.domain.room.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface RoomRepository extends MongoRepository<RoomDocument, String> {
    @Query("{id: '?0'}")
    RoomDocument findRoomById(String roomId);

    @Query("{vnum:  '?0'}")
    RoomDocument findRoomByVnum(String vnum);

    RoomDocument findRoomByNameAndAreaId(String name, String areaId);

    List<RoomDocument> findAllByAreaId(String areaId);
}
