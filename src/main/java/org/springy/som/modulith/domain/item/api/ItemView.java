package org.springy.som.modulith.domain.item.api;

import java.util.List;

public record ItemView(
        String id,
        String areaId,
        String vnum,
        String name,
        String shortDescription,
        String longDescription,
        String description,
        String itemType,
        String weight,
        String extraFlags,
        String wearFlags,
        String value,
        String level,
        List<String> affectData,
        List<String> extraDescr
) {}
