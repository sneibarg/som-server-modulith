package org.springy.som.modulith.domain.command.internal;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SocialRepository extends MongoRepository<SocialDocument, String> {
    @NotNull List<SocialDocument> findAll();

    @Query("{name: '?0'}")
    SocialDocument findSocialByName(String name);

    @Query("{id: '?0'}")
    SocialDocument findSocialById(String commandId);
}
