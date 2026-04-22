package org.springy.som.modulith.domain.special.api;

import java.util.List;

public record SpecialView(
        String id,
        String areaId,
        String mobVnum,
        String name,
        String comment,
        List<String> specialFunction
) {}
