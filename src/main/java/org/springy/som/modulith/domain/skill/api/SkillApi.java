package org.springy.som.modulith.domain.skill.api;

import org.springy.som.modulith.domain.skill.internal.SkillDocument;

import java.util.List;

public interface SkillApi {
    List<SkillDocument> getAllSkills();
    SkillDocument getSkillById(String id);
    SkillDocument getSkillByName(String name);
    SkillDocument createSkill(SkillDocument SkillDocument);
    SkillDocument saveSkillForId(String id, SkillDocument skillDocument);
    void deleteSkillById(String id);
    long deleteAllSkills();
}
