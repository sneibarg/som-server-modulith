package org.springy.som.modulith.domain.skill.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface SkillRepository extends MongoRepository<SkillDocument, String> {
    @Query("{id: '?0'}")
    SkillDocument findSkillById(String SkillId);

    @Query("{name:  '?0'}")
    SkillDocument findSkillByName(String skillName);
}
