package org.springy.som.modulith.controller;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springy.som.modulith.service.CommandService;
import org.springy.som.modulith.domain.command.Command;

import java.util.List;

public class CommandController {
    private final CommandService commandService;
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @GetMapping(path = "/api/v1/commands")
    @ResponseBody
    public List<Command> getCommands() {
        return commandService.getAllCommands();
    }

    @GetMapping(path = "/api/v1/command")
    @ResponseBody
    public Command getCommand(@Nullable @RequestParam String commandId,
                              @Nullable @RequestParam String commandName) {
        return commandService.getCommand(commandId, commandName);
    }

    @GetMapping(path = "/api/v1/commandbyname")
    @ResponseBody
    public Command getCommandByName(@RequestParam String commandName) {
        return commandService.getCommandByName(commandName);
    }
}
