package org.springy.som.modulith.agents.api;

public record AgentView(
        String id,
        String name,
        String accountName,
        String characterName,
        boolean enabled,
        String description
) {}
