package org.springy.som.modulith.domain.character.api;

import java.util.List;
import java.util.Map;

public record CharacterView(
        String id,
        String accountId,
        String name,
        String title,
        String description,
        String race,
        String sex,
        String roomId,
        String areaId,
        String guild,
        String role,
        Map<String, Object> equipped,
        Map<String, Boolean> promptFormat,
        Map<String, Object> characterClass,
        Map<String, Map<String, String>> characterFlags,
        Map<String, Integer> armorClass,
        Map<String, Object> characterAttributes,
        Map<String, Object> temporalMechanics,
        List<Map<String, Object>> inventory,
        List<Map<String, Object>> effects,
        List<Map<String, Object>> skills,
        List<Map<String, Object>> spells,
        Boolean cloaked,
        int hit,
        int maxHit,
        int mana,
        int maxMana,
        int movement,
        int maxMovement,
        int level,
        int gold,
        int silver,
        int trust,
        int invisLevel,
        int incogLevel
) {}
