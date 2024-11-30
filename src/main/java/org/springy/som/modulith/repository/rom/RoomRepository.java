package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.room.Room;

import java.util.List;


public interface RoomRepository extends MongoRepository<Room, String> {
    @Query("{id: '?0'}")
    Room findRoomById(String roomId);

    @Query("{vnum:  '?0'}")
    Room findRoomByVnum(String vnum);

    Room findRoomByNameAndAreaId(String name, String areaId);

    List<Room> findAllByAreaId(String areaId);
}
