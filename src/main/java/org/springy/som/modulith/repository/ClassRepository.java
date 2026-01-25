package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.clazz.RomClass;

public interface ClassRepository extends MongoRepository<RomClass, String> {
    @Query("{id: '?0'}")
    RomClass findRomClassById(String id);
}
