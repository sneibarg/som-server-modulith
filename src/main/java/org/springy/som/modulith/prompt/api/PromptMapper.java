package org.springy.som.modulith.prompt.api;

import org.springy.som.modulith.prompt.internal.PromptDocument;

public final class PromptMapper {
    private PromptMapper() {}

    public static PromptView toView(PromptDocument document) {
        return new PromptView(
                document.getName(),
                document.getModel(),
                document.getSystem(),
                document.getUser(),
                document.getJsonSchema(),
                document.getTokens());
    }
}
