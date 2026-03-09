package org.springy.som.modulith.domain.reset.api;

public record ResetView(
        String id,
        String areaId,
        String command,
        String arg1,
        String arg2,
        String arg3,
        String arg4,
        String comment
    ) {}
