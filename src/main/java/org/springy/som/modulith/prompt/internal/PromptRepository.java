package org.springy.som.modulith.prompt.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PromptRepository extends MongoRepository<PromptDocument, String> {
    @Query(value = "{ 'name': ?0 }", collation = "{ 'locale': 'en', 'strength': 2 }")
    List<PromptDocument> findAllByNameIgnoreCase(String name);
}
