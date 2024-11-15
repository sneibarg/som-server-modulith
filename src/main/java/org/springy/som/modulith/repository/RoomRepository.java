package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.room.RomRoom;


public interface RoomRepository extends MongoRepository<RomRoom, String> {
    @Query("{id: '?0'}")
    RomRoom findRoomById(String roomId);

    @Query("{vnum:  '?0'}")
    RomRoom findRoomByVnum(String vnum);
}
