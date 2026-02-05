package org.springy.som.modulith.domain.area.api;

import java.util.List;

public record AreaView(
        String id,
        String author,
        String name,
        String suggestedLevelRange,
        List<String> rooms,
        List<String> mobiles,
        List<String> objects,
        List<String> shops,
        List<String> resets,
        List<String> specials
) {}
