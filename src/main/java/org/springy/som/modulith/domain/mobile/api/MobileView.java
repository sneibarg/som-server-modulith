package org.springy.som.modulith.domain.mobile.api;

public record MobileView(
        String id,
        String areaId,
        String vnum,
        String name,
        String shortDescription,
        String longDescription,
        String description,
        String actFlags,
        String affectFlags,
        String alignment,
        String race,
        String sex,
        String startPos,
        String defaultPos,
        String hitroll,
        String damage,
        int level,
        int gold
) {}
