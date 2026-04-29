package org.springy.som.modulith.domain.room.api;

import java.util.List;
import java.util.Map;

public record RoomView(
        String id,
        String areaId,
        String vnum,
        String name,
        String description,
        String extraDescription,
        boolean pvp,
        boolean spawn,
        int spawnTimer,
        int spawnTime,
        int teleDelay,
        int roomFlags,
        int sectorType,
        List<String> exits,
        Map<String, String> mobiles
) {}
