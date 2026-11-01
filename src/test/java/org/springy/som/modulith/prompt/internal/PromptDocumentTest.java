package org.springy.som.modulith.prompt.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springy.som.modulith.prompt.api.PromptMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PromptDocumentTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void jsonMatchesThePromptCollectionDocument() throws Exception {
        Map<String, Object> exits = new LinkedHashMap<>();
        exits.put("type", "array");
        exits.put("items", Map.of("type", "string"));
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("exits", exits);
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of("exits"));
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("name", "Example");
        example.put("level", 12);
        Map<String, Map<String, Object>> tokens = new LinkedHashMap<>();
        tokens.put("identity", token("name, title, level, age, and hours played", example));
        tokens.put("trust", token("trust level", null));
        PromptDocument document = new PromptDocument();
        document.setId("mongo-id");
        document.setName("score");
        document.setModel("gpt-oss:20b");
        document.setSystem("Request: {intent}\n{response}");
        document.setUser("{score}");
        document.setJsonSchema(schema);
        document.setTokens(tokens);

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(document));
        JsonNode view = objectMapper.readTree(objectMapper.writeValueAsString(PromptMapper.toView(document)));

        assertThat(view).isEqualTo(json);
        assertThat(json.has("id")).isFalse();
        assertThat(json.get("jsonSchema").get("required").get(0).asText()).isEqualTo("exits");
        assertThat(json.get("tokens").get("trust").get("example").isNull()).isTrue();
        assertThat(json.get("tokens").get("identity").get("example").get("level").asInt()).isEqualTo(12);
    }

    private static Map<String, Object> token(String text, Object example) {
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("text", text);
        spec.put("example", example);
        return spec;
    }
}
