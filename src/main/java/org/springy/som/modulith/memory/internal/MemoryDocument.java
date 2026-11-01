package org.springy.som.modulith.memory.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document("Memories")
public class MemoryDocument {
    @Id
    @JsonIgnore
    private String id;
    private String agentName;
    private Instant savedAt;
    private List<Being> players = new ArrayList<>();
    private List<Being> agenticPlayers = new ArrayList<>();
    private List<Being> mobiles = new ArrayList<>();
    private List<Being> npcs = new ArrayList<>();
    private List<Being> aggressiveMobiles = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Command> commands = new ArrayList<>();
    private List<Consider> considers = new ArrayList<>();
    private List<Conversation> conversations = new ArrayList<>();

    @Data
    public static class Being {
        private String name;
        private String kind;
        private boolean agentic;
        private String roomName;
        private boolean aggressive;
        private List<String> auras = new ArrayList<>();
    }

    @Data
    public static class Room {
        private String name;
        private String description;
        private List<String> exits = new ArrayList<>();
    }

    @Data
    public static class Command {
        private String name;
        private String category;
        private String helpText;
    }

    @Data
    public static class Consider {
        private String target;
        private String subject;
        private String kind;
        private String outlook;
        private String verdict;
        private String text;
    }

    @Data
    public static class Conversation {
        private String topic;
        private String location;
        private String locationName;
        private List<String> participants = new ArrayList<>();
        private int interest;
    }
}
