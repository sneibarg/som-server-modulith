package org.springy.som.modulith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.repository.CommandRepository;

import java.util.List;

@Slf4j
@Service
public class CommandService {
    private final CommandRepository commandRepository;

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public List<Command> getAllCommands() {
        return commandRepository.findAll();
    }

    public Command getCommand(@RequestParam String commandId, @RequestParam String commandName) {
        if (null != commandId) return commandRepository.findCommandById(commandId);
        if (null != commandName) return commandRepository.findCommandByName(commandName);
        return null;
    }

    public Command getCommandByName(@RequestParam String commandName) {
        return commandRepository.findCommandByName(commandName);
    }

    public Command getCommandById(@RequestParam String commandId) {
        return commandRepository.findCommandById(commandId);
    }
}
