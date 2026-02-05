package org.springy.som.modulith.domain.reset.api;

import java.util.List;

public record ResetView(
        String id,
        String areaId,
        String resetType,
        String comment,
        List<String> args
) {}
