package org.springy.som.modulith.agents.api;

import org.springy.som.modulith.agents.internal.AgentDocument;

import java.util.List;

public interface AgentApi {
    List<AgentDocument> getAll();

    AgentDocument get(String name);

    AgentDocument save(String name, AgentDocument document);
}
