package org.springy.som.modulith.domain.item.api;

import java.util.List;

public record ItemView(
        String id,
        String areaId,
        String vnum,
        String name,
        String shortDescription,
        String longDescription,
        String itemType,
        String extraFlags,
        String wearFlags,
        String value,
        String level,
        String value0,
        String value1,
        String value2,
        String value3,
        String value4,
        String weight,
        String cost,
        String condition,
        List<String> affectData,
        List<String> extraDescr
) {}
