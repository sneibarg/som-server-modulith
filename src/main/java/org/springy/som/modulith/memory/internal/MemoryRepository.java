package org.springy.som.modulith.memory.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.List;

public interface MemoryRepository extends MongoRepository<MemoryDocument, String> {
    @Query(value = "{ 'agentName': ?0 }", collation = "{ 'locale': 'en', 'strength': 2 }")
    List<MemoryDocument> findAllByAgentNameIgnoreCase(String agentName);

    void deleteBySavedAtIsNull();

    void deleteBySavedAtBefore(Instant savedAt);
}
