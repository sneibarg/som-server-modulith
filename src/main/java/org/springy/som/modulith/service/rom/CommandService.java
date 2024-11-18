package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.rom.command.Command;
import org.springy.som.modulith.repository.rom.CommandRepository;

import java.util.List;

@Slf4j
@RestController
public class CommandService {
    private final CommandRepository commandRepository;

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @GetMapping(path = "/api/v1/commands")
    @ResponseBody
    public List<Command> getCommands() {
        return commandRepository.findAll();
    }

    @GetMapping(path = "/api/v1/command")
    @ResponseBody
    public Command getCommand(@Nullable @RequestParam String commandId,
                              @Nullable @RequestParam String commandName) {
        if (null != commandId)
            return commandRepository.findCommandById(commandId);
        if (null != commandName)
            return commandRepository.findCommandByName(commandName);
        return null;
    }

    @GetMapping(path = "/api/v1/commandbyname")
    @ResponseBody
    public Command getCommandByName(@RequestParam String commandName) {
        return commandRepository.findCommandByName(commandName);
    }
}
