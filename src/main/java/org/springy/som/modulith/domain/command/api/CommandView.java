package org.springy.som.modulith.domain.command.api;

import java.util.List;

public record CommandView(
        String id,
        String name,
        String message,
        String role,
        String usage,
        String skillId,
        String shortcuts,
        List<String> lambda,
        List<String> function,
        boolean enabled
) {}
