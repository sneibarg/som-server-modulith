package org.springy.som.modulith.domain.special.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SpecialRepository extends MongoRepository<SpecialDocument, String> {
    @Query("{id: '?0'}")
    SpecialDocument findSpecialById(String specialId);

    @Query("{mobVnum: '?0'}")
    List<SpecialDocument> findSpecialsByMobVnum(String mobVnum);

    @Query("{areaId: '?0'}")
    List<SpecialDocument> findSpecialsByAreaId(String areaId);

    @Query("{areaId: '?0', mobVnum: '?1'}")
    SpecialDocument findSpecialByAreaIdAndMobVnum(String areaId, String mobVnum);
}
