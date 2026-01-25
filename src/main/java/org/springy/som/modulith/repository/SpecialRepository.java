package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.special.Special;

public interface SpecialRepository extends MongoRepository<Special, String> {
    @Query("{id: '?0'}")
    Special findSpecialById(String specialId);
}
