package org.springy.som.modulith.knowledge.api;

import org.springy.som.modulith.knowledge.internal.KnowledgeDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class KnowledgeMapper {
    private KnowledgeMapper() {}

    public static KnowledgeView toView(KnowledgeDocument document) {
        return new KnowledgeView(
                document.getCharacterId(),
                document.getCharacterName(),
                document.getAgentName(),
                document.getLoginLines(),
                mapList(document.getHelpTopics(), KnowledgeMapper::toHelpTopic),
                mapList(document.getAreas(), KnowledgeMapper::toArea),
                mapList(document.getMobiles(), KnowledgeMapper::toBeing),
                mapList(document.getNpcs(), KnowledgeMapper::toBeing),
                mapList(document.getPlayers(), KnowledgeMapper::toPlayer),
                toRoom(document.getRoom()));
    }

    private static KnowledgeView.HelpTopic toHelpTopic(KnowledgeDocument.HelpTopic topic) {
        return new KnowledgeView.HelpTopic(
                topic.getCommand(),
                topic.getKeyword(),
                topic.getText(),
                topic.getRaw(),
                topic.isMissing(),
                mapList(topic.getTopics(), KnowledgeMapper::toArticle));
    }

    private static KnowledgeView.HelpArticle toArticle(KnowledgeDocument.HelpArticle article) {
        return new KnowledgeView.HelpArticle(article.getKeyword(), article.getText());
    }

    private static KnowledgeView.KnownArea toArea(KnowledgeDocument.KnownArea area) {
        return new KnowledgeView.KnownArea(area.getName(), area.getAuthor(), area.getLevelRange());
    }

    private static KnowledgeView.Being toBeing(KnowledgeDocument.Being being) {
        return new KnowledgeView.Being(
                being.getName(),
                being.getKind(),
                being.getPose(),
                being.getRoomName(),
                being.getDetail(),
                being.getAuras());
    }

    private static KnowledgeView.Player toPlayer(KnowledgeDocument.Player player) {
        return new KnowledgeView.Player(
                player.getName(),
                player.getLevel(),
                player.getRace(),
                player.getCharacterClass(),
                player.getTitle(),
                player.isAgentic(),
                player.isSelfCharacter(),
                player.getRoomName(),
                player.getFlags(),
                player.getAuras());
    }

    private static KnowledgeView.Room toRoom(KnowledgeDocument.Room room) {
        if (room == null) {
            return null;
        }
        return new KnowledgeView.Room(room.getName(), room.getDescription(), room.getExits(), room.getItemLines());
    }

    private static <T, R> List<R> mapList(List<T> values, Function<T, R> mapper) {
        if (values == null) {
            return null;
        }
        List<R> mapped = new ArrayList<>(values.size());
        for (T value : values) {
            mapped.add(value == null ? null : mapper.apply(value));
        }
        return mapped;
    }
}
