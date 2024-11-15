package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.area.Area;


public interface AreaRepository extends MongoRepository<Area, String> {
    @Query("{id: '?0'}")
    Area findAreaByAreaId(String areaId);
}
