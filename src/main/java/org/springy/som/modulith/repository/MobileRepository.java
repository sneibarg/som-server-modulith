package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.mobile.Mobile;

import java.util.List;


public interface MobileRepository extends MongoRepository<Mobile, String> {
    @Query("{id: '?0'}")
    Mobile findMobileById(String mobileId);

    @Query("{raceId: '?0'}")
    List<Mobile> findAllByRace(String raceId);

    @Query("{classId: '?0'}")
    List<Mobile> findAllByClass(String classId);

    @Query("{ 'level': { $gte: 1, $lte: 10 } }")
    List<Mobile> findMobilesByLevelRange(int min, int max);

}
