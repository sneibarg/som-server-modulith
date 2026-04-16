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
        String characterClass,
        String roomId,
        String areaId,
        String guild,
        String role,
        String characterFlags,
        String armorClass,
        String characterAttributes,
        String temporalMechanics,
        Boolean cloaked,
        List<String> inventory,
        Map<String, Boolean> promptFormat,
        int hit,
        int maxHit,
        int mana,
        int maxMana,
        int movement,
        int maxMovement,
        int level,
        int experience,
        int accumulatedExperience,
        int gold,
        int silver,
        int trust
) {}
