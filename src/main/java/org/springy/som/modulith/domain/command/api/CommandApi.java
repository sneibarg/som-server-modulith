package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.CommandDocument;

import java.util.List;

public interface CommandApi {
    List<CommandDocument> getAllCommands();
    CommandDocument getCommandByName(String commandName);
    CommandDocument getCommandById(String commandId);
    CommandDocument createCommand(CommandDocument commandDocument);
    CommandDocument saveCommandForId(String id, CommandDocument commandDocument);
    void deleteCommandById(String id);
    long deleteAllCommands();
}
