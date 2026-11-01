package org.springy.som.modulith.memory.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springy.som.modulith.memory.api.MemoryMapper;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryDocumentTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void jsonOmitsTheMongoIdAndKeepsSavedAt() throws Exception {
        MemoryDocument.Being mobile = new MemoryDocument.Being();
        mobile.setName("goblin");
        mobile.setKind("mobile");
        mobile.setAgentic(false);
        mobile.setRoomName("Temple");
        mobile.setAggressive(true);
        mobile.setAuras(List.of("glowing"));
        MemoryDocument.Room room = new MemoryDocument.Room();
        room.setName("Temple");
        room.setDescription("A quiet square.");
        room.setExits(List.of("north"));
        MemoryDocument.Command command = new MemoryDocument.Command();
        command.setName("look");
        command.setCategory("info");
        command.setHelpText("Look around.");
        MemoryDocument.Consider consider = new MemoryDocument.Consider();
        consider.setTarget("goblin");
        consider.setSubject("goblin");
        consider.setKind("mobile");
        consider.setOutlook("safe");
        consider.setVerdict("easy_kill");
        consider.setText("The goblin looks like an easy kill.");
        MemoryDocument.Conversation conversation = new MemoryDocument.Conversation();
        conversation.setTopic("gate");
        conversation.setLocation("room");
        conversation.setLocationName("Temple");
        conversation.setParticipants(List.of("Ada"));
        conversation.setInterest(2);
        MemoryDocument document = new MemoryDocument();
        document.setId("mongo-id");
        document.setAgentName("Ada");
        document.setSavedAt(Instant.parse("2026-11-01T14:00:00Z"));
        document.setMobiles(List.of(mobile));
        document.setRooms(List.of(room));
        document.setCommands(List.of(command));
        document.setConsiders(List.of(consider));
        document.setConversations(List.of(conversation));

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(document));
        JsonNode view = objectMapper.readTree(objectMapper.writeValueAsString(MemoryMapper.toView(document)));
        assertThat(view).isEqualTo(json);

        assertThat(json.has("id")).isFalse();
        assertThat(json.get("agentName").asText()).isEqualTo("Ada");
        assertThat(json.get("savedAt").asText()).isEqualTo("2026-11-01T14:00:00Z");
        assertThat(json.get("mobiles").get(0).get("auras").get(0).asText()).isEqualTo("glowing");
        assertThat(json.get("commands").get(0).get("helpText").asText()).isEqualTo("Look around.");
        assertThat(json.get("considers").get(0).has("estimated")).isFalse();
        assertThat(json.get("conversations").get(0).get("locationName").asText()).isEqualTo("Temple");
        assertThat(json.has("agenticPlayers")).isTrue();
    }
}
