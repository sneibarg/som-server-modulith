package org.springy.som.modulith.domain.command.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CommandRepository extends MongoRepository<CommandDocument, String> {
    List<CommandDocument> findAll();

    @Query("{name: '?0'}")
    CommandDocument findCommandByName(String name);

    @Query("{id: '?0'}")
    CommandDocument findCommandById(String commandId);
}
