package org.springy.som.modulith.domain.spell.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;


public interface SpellRepository extends MongoRepository<SpellDocument, String> {
    @Query("{id: '?0'}")
    SpellDocument findSpellById(String SkillId);

    @Query("{name:  '?0'}")
    SpellDocument findSpellByName(String skillName);
}
