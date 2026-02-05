package org.springy.som.modulith.domain.race.api;

import java.util.List;

public record RomRaceView(
        String id,
        String name,
        String size,
        List<String> skills,
        List<Integer> classMultiplier,
        int points,
        int str,
        int maxStr,
        int INT,
        int maxInt,
        int con,
        int maxCon,
        int wis,
        int maxWis,
        int dex,
        int maxDex
) {}
