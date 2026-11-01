package org.springy.som.modulith.knowledge.api;

import java.util.List;

public record KnowledgeView(
        String characterId,
        String characterName,
        String agentName,
        List<String> loginLines,
        List<HelpTopic> helpTopics,
        List<Command> commands,
        List<KnownArea> areas,
        List<Being> mobiles,
        List<Being> npcs,
        List<Player> players,
        Room room
) {
    public record HelpTopic(
            String command,
            String keyword,
            String text,
            String raw,
            boolean missing,
            List<HelpArticle> topics
    ) {}

    public record HelpArticle(String keyword, String text) {}

    public record Command(String name, String category, String helpText) {}

    public record KnownArea(String name, String author, String levelRange) {}

    public record Being(
            String name,
            String kind,
            String pose,
            String roomName,
            String detail,
            List<String> auras
    ) {}

    public record Player(
            String name,
            Integer level,
            String race,
            String characterClass,
            String title,
            boolean agentic,
            boolean selfCharacter,
            String roomName,
            String flags,
            List<String> auras
    ) {}

    public record Room(
            String name,
            String description,
            List<String> exits,
            List<String> itemLines
    ) {}
}
