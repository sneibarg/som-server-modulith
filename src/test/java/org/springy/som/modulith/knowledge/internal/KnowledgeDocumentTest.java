package org.springy.som.modulith.knowledge.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springy.som.modulith.knowledge.api.KnowledgeMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KnowledgeDocumentTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void jsonUsesTheStoredFieldNames() throws Exception {
        KnowledgeDocument.HelpArticle article = new KnowledgeDocument.HelpArticle();
        article.setKeyword("look");
        article.setText("Look around.");
        KnowledgeDocument.HelpTopic topic = new KnowledgeDocument.HelpTopic();
        topic.setCommand("look");
        topic.setKeyword("look");
        topic.setText("Look around.");
        topic.setRaw("look");
        topic.setMissing(false);
        topic.setTopics(List.of(article));
        KnowledgeDocument.KnownArea area = new KnowledgeDocument.KnownArea();
        area.setName("Midgaard");
        area.setAuthor("Ada");
        area.setLevelRange("1-10");
        KnowledgeDocument.Being mobile = new KnowledgeDocument.Being();
        mobile.setName("goblin");
        mobile.setKind("mobile");
        mobile.setPose("standing");
        mobile.setRoomName("Temple");
        mobile.setDetail("");
        mobile.setAuras(List.of("glowing"));
        KnowledgeDocument.Player player = new KnowledgeDocument.Player();
        player.setName("Ada");
        player.setLevel(5);
        player.setRace("human");
        player.setCharacterClass("mage");
        player.setTitle("the bright");
        player.setAgentic(true);
        player.setSelfCharacter(false);
        player.setRoomName("Temple");
        player.setFlags("");
        player.setAuras(List.of("humming"));
        KnowledgeDocument.Room room = new KnowledgeDocument.Room();
        room.setName("Temple");
        room.setDescription("A quiet square.");
        room.setExits(List.of("north"));
        room.setItemLines(List.of("a fountain"));
        KnowledgeDocument document = new KnowledgeDocument();
        document.setCharacterId("hero");
        document.setCharacterName("Ada");
        document.setAgentName("guide");
        document.setLoginLines(List.of("Welcome"));
        document.setHelpTopics(List.of(topic));
        document.setAreas(List.of(area));
        document.setMobiles(List.of(mobile));
        document.setNpcs(List.of());
        document.setPlayers(List.of(player));
        document.setRoom(room);

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(document));
        JsonNode view = objectMapper.readTree(objectMapper.writeValueAsString(KnowledgeMapper.toView(document)));
        assertThat(view).isEqualTo(json);

        assertThat(json.get("characterId").asText()).isEqualTo("hero");
        assertThat(json.has("id")).isFalse();
        assertThat(json.get("helpTopics").get(0).get("topics").get(0).get("keyword").asText()).isEqualTo("look");
        assertThat(json.get("areas").get(0).get("levelRange").asText()).isEqualTo("1-10");
        assertThat(json.get("mobiles").get(0).get("auras").get(0).asText()).isEqualTo("glowing");
        assertThat(json.get("players").get(0).get("characterClass").asText()).isEqualTo("mage");
        assertThat(json.get("players").get(0).get("selfCharacter").asBoolean()).isFalse();
        assertThat(json.get("players").get(0).get("auras").get(0).asText()).isEqualTo("humming");
        assertThat(json.get("room").get("itemLines").get(0).asText()).isEqualTo("a fountain");
    }
}
