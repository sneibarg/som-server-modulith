package org.springy.som.modulith.knowledge.api;

import org.springy.som.modulith.knowledge.internal.KnowledgeDocument;

public interface KnowledgeApi {
    KnowledgeDocument get(String characterId);

    KnowledgeDocument save(String characterId, KnowledgeDocument document);
}
