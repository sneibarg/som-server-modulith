package org.springy.som.modulith.agents.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AgentRepository extends MongoRepository<AgentDocument, String> {
    @Query(value = "{ 'name': ?0 }", collation = "{ 'locale': 'en', 'strength': 2 }")
    List<AgentDocument> findAllByNameIgnoreCase(String name);
}
