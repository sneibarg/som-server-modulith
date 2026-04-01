package org.springy.som.modulith.domain.command.internal;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface HelpRepository extends MongoRepository<HelpDocument, String> {
    @NotNull List<HelpDocument> findAll();

    @Query("{keyword: '?0'}")
    HelpDocument findHelpByKeyword(String keyword);

    @Query("{id: '?0'}")
    HelpDocument findHelpById(String helpId);
}
