package org.springy.som.modulith.domain.race.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RaceRepository  extends MongoRepository<RaceDocument, String> {
    @Query("{id: '?0'}")
    RaceDocument findRomRaceById(String id);
}
