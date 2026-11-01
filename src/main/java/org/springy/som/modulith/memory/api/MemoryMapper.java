package org.springy.som.modulith.memory.api;

import org.springy.som.modulith.memory.internal.MemoryDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class MemoryMapper {
    private MemoryMapper() {}

    public static MemoryView toView(MemoryDocument document) {
        return new MemoryView(
                document.getAgentName(),
                document.getSavedAt(),
                mapList(document.getPlayers(), MemoryMapper::toBeing),
                mapList(document.getAgenticPlayers(), MemoryMapper::toBeing),
                mapList(document.getMobiles(), MemoryMapper::toBeing),
                mapList(document.getNpcs(), MemoryMapper::toBeing),
                mapList(document.getAggressiveMobiles(), MemoryMapper::toBeing),
                mapList(document.getRooms(), MemoryMapper::toRoom),
                mapList(document.getCommands(), MemoryMapper::toCommand),
                mapList(document.getConsiders(), MemoryMapper::toConsider),
                mapList(document.getConversations(), MemoryMapper::toConversation));
    }

    private static MemoryView.Being toBeing(MemoryDocument.Being being) {
        return new MemoryView.Being(
                being.getName(),
                being.getKind(),
                being.isAgentic(),
                being.getRoomName(),
                being.isAggressive(),
                being.getAuras());
    }

    private static MemoryView.Room toRoom(MemoryDocument.Room room) {
        return new MemoryView.Room(room.getName(), room.getDescription(), room.getExits());
    }

    private static MemoryView.Command toCommand(MemoryDocument.Command command) {
        return new MemoryView.Command(command.getName(), command.getCategory(), command.getHelpText());
    }

    private static MemoryView.Consider toConsider(MemoryDocument.Consider consider) {
        return new MemoryView.Consider(
                consider.getTarget(),
                consider.getSubject(),
                consider.getKind(),
                consider.getOutlook(),
                consider.getVerdict(),
                consider.getText());
    }

    private static MemoryView.Conversation toConversation(MemoryDocument.Conversation conversation) {
        return new MemoryView.Conversation(
                conversation.getTopic(),
                conversation.getLocation(),
                conversation.getLocationName(),
                conversation.getParticipants(),
                conversation.getInterest());
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
