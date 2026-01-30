package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.exception.clazz.RomClassNotFoundException;
import org.springy.som.modulith.exception.clazz.RomClassPersistenceException;
import org.springy.som.modulith.exception.command.CommandPersistenceException;
import org.springy.som.modulith.exception.command.InvalidCommandException;
import org.springy.som.modulith.repository.CommandRepository;

import java.util.List;

@Slf4j
@Service
public class CommandService {
    private final CommandRepository commandRepository;

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRomClassesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Command> getAllCommands() {
        return commandRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Command getCommandByName(@RequestParam String commandName) {
        return commandRepository.findCommandByName(commandName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Command getCommandById(@RequestParam String commandId) {
        return commandRepository.findCommandById(commandId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Command createCommand(@Valid @RequestBody Command command) {
        requireCommand(command);

        try {
            // if (areaRepository.existsById(area.getAreaId())) throw new AreaConflictException(...)
            return commandRepository.save(command);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createCommand areaId={}", safeId(command), ex);
            throw new RomClassPersistenceException("Failed to create command"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Command saveCommandForId(String id, Command command) {
        requireId(id);
        requireCommand(command);

        return commandRepository.save(getCommandById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteCommandById(String id) {
        requireId(id);

        try {
            if (!commandRepository.existsById(id)) {
                throw new RomClassNotFoundException(id);
            }
            commandRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteCommandById id={}", id, ex);
            throw new CommandPersistenceException("Failed to delete command: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllCommands() {
        try {
            long itemCount = commandRepository.count();
            commandRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllCommands", ex);
            throw new CommandPersistenceException("Failed to delete all commands "+ ex);
        }
    }

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidCommandException("Command id must be provided");
        }
    }

    private static void requireCommand(Command command) {
        if (command == null) {
            throw new InvalidCommandException("Command must be provided");
        }

        requireId(command.getId());
    }

    private static String safeId(Command command) {
        try {
            return command.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<Command> getAllCommandsFallback(Throwable t) {
        log.warn("Fallback getAllCommands due to {}", t.toString());
        return List.of();
    }

    private Command getRomClassByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllCommandsById id={} due to {}", id, t.toString());
        throw new CommandPersistenceException("Command lookup temporarily unavailable: " + id+" "+t);
    }
}
