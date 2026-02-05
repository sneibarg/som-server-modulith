package org.springy.som.modulith.domain.special.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SpecialRepository extends MongoRepository<SpecialDocument, String> {
    @Query("{id: '?0'}")
    SpecialDocument findSpecialById(String specialId);
}
