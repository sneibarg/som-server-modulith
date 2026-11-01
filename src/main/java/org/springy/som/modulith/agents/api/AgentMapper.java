package org.springy.som.modulith.agents.api;

import org.springy.som.modulith.agents.internal.AgentDocument;

public final class AgentMapper {
    private AgentMapper() {}

    public static AgentView toView(AgentDocument document) {
        return new AgentView(
                document.getId(),
                document.getName(),
                document.getAccountName(),
                document.getCharacterName(),
                document.getEnabled() == null || document.getEnabled(),
                document.getDescription());
    }
}
