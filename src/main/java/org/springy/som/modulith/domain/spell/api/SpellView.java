package org.springy.som.modulith.domain.spell.api;

import java.util.List;
import java.util.Map;

public record SpellView(
        String id,
        String name,
        String kind,
        String handlerId,
        String target,
        String minPosition,
        String nounDamage,
        String functionName,
        List<Map<String, Object>> guards,
        Map<String, Object> payload,
        Map<String, Integer> levelByClass,
        Map<String, Integer> ratingByClass,
        int slot,
        int minMana,
        int beats,
        List<Map<String, Object>> affectData,
        List<String> lambdas

) {}
