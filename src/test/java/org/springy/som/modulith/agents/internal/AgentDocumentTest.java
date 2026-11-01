package org.springy.som.modulith.agents.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springy.som.modulith.agents.api.AgentMapper;

import static org.assertj.core.api.Assertions.assertThat;

class AgentDocumentTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void jsonMatchesTheAgentCollectionDocument() throws Exception {
        AgentDocument document = new AgentDocument();
        document.setId("66f0a1b2c3d4e5f607182930");
        document.setName("example-agent");
        document.setAccountName("agent-account");
        document.setCharacterName("Example");
        document.setEnabled(true);
        document.setDescription("A guide.");

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(document));
        JsonNode view = objectMapper.readTree(objectMapper.writeValueAsString(AgentMapper.toView(document)));

        assertThat(view).isEqualTo(json);
        assertThat(json.get("id").asText()).isEqualTo("66f0a1b2c3d4e5f607182930");
        assertThat(json.get("accountName").asText()).isEqualTo("agent-account");
        assertThat(json.get("characterName").asText()).isEqualTo("Example");
        assertThat(json.get("enabled").asBoolean()).isTrue();
        assertThat(json.has("password")).isFalse();
    }
}
