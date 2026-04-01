package org.springy.som.modulith.domain.room.api;

import java.util.List;

public record RoomView(
        String id,
        String areaId,
        String vnum,
        String name,
        String description,
        String exits,
        boolean pvp,
        boolean spawn,
        int spawnTimer,
        int spawnTime,
        int teleDelay,
        int roomFlags,
        int sectorType,
        List<String> mobiles,
        List<String> extraDescription
) {}
