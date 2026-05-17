package org.springy.som.modulith.domain.command.api;

import java.util.List;
import java.util.Map;

public record CommandView(
        String id,
        String name,
        String message,
        String role,
        String usage,
        String skillId,
        String shortcuts,
        String position,
        String log,
        String help,
        String level,
        Map<String, Object> payload,
        List<Map<String, Object>> guards,
        List<String> lambdas,
        List<String> function,
        boolean enabled,
        boolean pipeline,
        int maxArguments
) {}
