package org.springy.som.modulith.prompt.api;

import java.util.Map;

public record PromptView(
        String name,
        String model,
        String system,
        String user,
        Map<String, Object> jsonSchema,
        Map<String, Map<String, Object>> tokens
) {}
