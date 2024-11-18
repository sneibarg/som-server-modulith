package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.special.Special;

public interface SpecialRepository extends MongoRepository<Special, String> {
    @Query("{id: '?0'}")
    Special findSpecialById(String specialId);
}
