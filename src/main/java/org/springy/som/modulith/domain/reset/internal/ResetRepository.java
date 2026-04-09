package org.springy.som.modulith.domain.reset.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ResetRepository extends MongoRepository<ResetDocument, String> {
    @Query("{id: '?0'}")
    ResetDocument findResetById(String resetId);

    @Query("{areaId: '?0'}")
    List<ResetDocument> findAllByAreaId(String areaId);

    long deleteAllByAreaId(String areaId);
}
