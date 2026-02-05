package org.springy.som.modulith.domain.reset.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ResetRepository extends MongoRepository<ResetDocument, String> {
    @Query("{id: '?0'}")
    ResetDocument findResetById(String resetId);
}
