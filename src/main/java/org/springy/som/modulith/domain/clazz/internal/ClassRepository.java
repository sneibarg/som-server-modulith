package org.springy.som.modulith.domain.clazz.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ClassRepository extends MongoRepository<ClassDocument, String> {
    @Query("{id: '?0'}")
    ClassDocument findRomClassById(String id);
}
