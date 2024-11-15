package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.area.RomArea;


public interface RomAreaRepository extends MongoRepository<RomArea, String> {
    @Query("{id: '?0'}")
    RomArea findAreaByAreaId(String areaId);
}
