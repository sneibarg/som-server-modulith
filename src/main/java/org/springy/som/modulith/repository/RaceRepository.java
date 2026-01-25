package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.race.RomRace;

public interface RaceRepository  extends MongoRepository<RomRace, String> {
    @Query("{id: '?0'}")
    RomRace findRomRaceById(String id);
}
