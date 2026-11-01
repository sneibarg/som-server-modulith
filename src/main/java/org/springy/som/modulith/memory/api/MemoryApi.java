package org.springy.som.modulith.memory.api;

import org.springy.som.modulith.memory.internal.MemoryDocument;

public interface MemoryApi {
    MemoryDocument get(String agentName);

    MemoryDocument save(String agentName, MemoryDocument document);
}
