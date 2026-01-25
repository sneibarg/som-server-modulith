package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.reset.Reset;

public interface ResetRepository extends MongoRepository<Reset, String> {
    @Query("{id: '?0'}")
    Reset findResetById(String resetId);
}
