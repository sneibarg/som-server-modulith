package org.springy.som.modulith.domain.mobile.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


public interface MobileRepository extends MongoRepository<MobileDocument, String> {
    @Query("{id: '?0'}")
    MobileDocument findMobileById(String mobileId);

    @Query("{name:  '?0'}")
    MobileDocument findMobileByName(String name);

    @Query("{raceId: '?0'}")
    List<MobileDocument> findAllByRace(String raceId);

    @Query("{classId: '?0'}")
    List<MobileDocument> findAllByClass(String classId);

    @Query("{ 'level': { $gte: 1, $lte: 10 } }")
    List<MobileDocument> findMobilesByLevelRange(int min, int max);

    long deleteAllByAreaId(String areaId);
}
