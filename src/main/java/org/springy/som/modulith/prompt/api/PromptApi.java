package org.springy.som.modulith.prompt.api;

import org.springy.som.modulith.prompt.internal.PromptDocument;

import java.util.List;

public interface PromptApi {
    List<PromptDocument> getAll();

    PromptDocument get(String name);

    PromptDocument save(String name, PromptDocument document);
}
