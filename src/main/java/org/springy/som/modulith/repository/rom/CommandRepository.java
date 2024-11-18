package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.command.Command;

import java.util.List;

public interface CommandRepository extends MongoRepository<Command, String> {
    List<Command> findAll();

    @Query("{name: '?0'}")
    Command findCommandByName(String name);

    @Query("{id: '?0'}")
    Command findCommandById(String commandId);
}
