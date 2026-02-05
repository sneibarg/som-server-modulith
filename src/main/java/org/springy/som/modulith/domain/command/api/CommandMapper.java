package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.CommandDocument;

public final class CommandMapper {

    private CommandMapper() {}

    public static CommandView toView(CommandDocument doc) {
        return new CommandView(
                doc.getId(),
                doc.getName(),
                doc.getMessage(),
                doc.getRole(),
                doc.getUsage(),
                doc.getSkillId(),
                doc.getShortcuts(),
                doc.getLambda(),
                doc.getFunction(),
                doc.isEnabled()
        );
    }
}
