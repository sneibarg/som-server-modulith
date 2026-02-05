package org.springy.som.modulith.domain.area.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface AreaRepository extends MongoRepository<AreaDocument, String> {
    @Query("{id: '?0'}")
    AreaDocument findAreaByAreaId(String areaId);
}
