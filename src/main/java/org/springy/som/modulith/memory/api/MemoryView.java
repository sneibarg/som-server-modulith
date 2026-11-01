package org.springy.som.modulith.memory.api;

import java.time.Instant;
import java.util.List;

public record MemoryView(
        String agentName,
        Instant savedAt,
        List<Being> players,
        List<Being> agenticPlayers,
        List<Being> mobiles,
        List<Being> npcs,
        List<Being> aggressiveMobiles,
        List<Room> rooms,
        List<Command> commands,
        List<Consider> considers,
        List<Conversation> conversations
) {
    public record Being(
            String name,
            String kind,
            boolean agentic,
            String roomName,
            boolean aggressive,
            List<String> auras
    ) {}

    public record Room(String name, String description, List<String> exits) {}

    public record Command(String name, String category, String helpText) {}

    public record Consider(
            String target,
            String subject,
            String kind,
            String outlook,
            String verdict,
            String text
    ) {}

    public record Conversation(
            String topic,
            String location,
            String locationName,
            List<String> participants,
            int interest
    ) {}
}
