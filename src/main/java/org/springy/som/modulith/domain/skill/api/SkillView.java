package org.springy.som.modulith.domain.skill.api;

import java.util.Map;

public record SkillView(
        String id,
        String name,
        String kind,
        String handlerId,
        String target,
        String minPosition,
        String nounDamage,
        String msgOff,
        String msgObj,
        Map<String, Integer> levelByClass,
        Map<String, Integer> ratingByClass,
        int slot,
        int minMana,
        int beats

) {}
