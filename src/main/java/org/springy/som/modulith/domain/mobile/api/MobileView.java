package org.springy.som.modulith.domain.mobile.api;

import java.util.Map;

public record MobileView(
        String id,
        String areaId,
        String vnum,
        String name,
        String shortDescription,
        String longDescription,
        String description,
        String race,
        String alignment,
        String group,
        String damType,
        String hitDice,
        String manaDice,
        String damageDice,
        String armorClass,
        String startPos,
        String defaultPos,
        String sex,
        String size,
        String material,
        String flags,
        Map<String, Object> statusFlags,
        int level,
        int hitroll,
        int gold,
        int silver
) {}
