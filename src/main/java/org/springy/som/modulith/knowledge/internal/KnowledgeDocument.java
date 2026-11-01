package org.springy.som.modulith.knowledge.internal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document("AgentKnowledge")
public class KnowledgeDocument {
    @Id
    private String characterId;
    private String characterName;
    private String agentName;
    private List<String> loginLines = new ArrayList<>();
    private List<HelpTopic> helpTopics = new ArrayList<>();
    private List<KnownArea> areas = new ArrayList<>();
    private List<Being> mobiles = new ArrayList<>();
    private List<Being> npcs = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Room room;

    @Data
    public static class HelpTopic {
        private String command;
        private String keyword;
        private String text;
        private String raw;
        private boolean missing;
        private List<HelpArticle> topics = new ArrayList<>();
    }

    @Data
    public static class HelpArticle {
        private String keyword;
        private String text;
    }

    @Data
    public static class KnownArea {
        private String name;
        private String author;
        private String levelRange;
    }

    @Data
    public static class Being {
        private String name;
        private String kind;
        private String pose;
        private String roomName;
        private String detail;
        private List<String> auras = new ArrayList<>();
    }

    @Data
    public static class Player {
        private String name;
        private Integer level;
        private String race;
        private String characterClass;
        private String title;
        private boolean agentic;
        private boolean selfCharacter;
        private String roomName;
        private String flags;
        private List<String> auras = new ArrayList<>();
    }

    @Data
    public static class Room {
        private String name;
        private String description;
        private List<String> exits = new ArrayList<>();
        private List<String> itemLines = new ArrayList<>();
    }
}
