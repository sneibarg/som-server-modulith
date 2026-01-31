package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.exception.clazz.RomClassNotFoundException;
import org.springy.som.modulith.exception.clazz.RomClassPersistenceException;
import org.springy.som.modulith.exception.command.CommandPersistenceException;
import org.springy.som.modulith.repository.CommandRepository;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.commandIdMissing;
import static org.springy.som.modulith.util.DomainGuards.commandMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class CommandService {
    private final CommandRepository commandRepository;

    public CommandService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllCommandsFallback")
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
        requireEntityWithId(command, Command::getId, commandMissing(), commandIdMissing());

        try {
            // if (commandRepository.existsById(command.getId())) throw new CommandConflictException(...)
            return commandRepository.save(command);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createCommand areaId={}", safeId(command, Command::getId), ex);
            throw new RomClassPersistenceException("Failed to create command"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Command saveCommandForId(String id, Command command) {
        requireText(id, commandIdMissing());
        requireEntityWithId(command, Command::getId, commandMissing(), commandIdMissing());

        return commandRepository.save(getCommandById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteCommandById(String id) {
        requireText(id, commandIdMissing());

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

    private List<Command> getAllCommandsFallback(Throwable t) {
        log.warn("Fallback getAllCommands due to {}", t.toString());
        return List.of();
    }

    private Command getCommandByIdFallback(String id, Throwable t) {
        log.warn("Fallback getCommandById id={} due to {}", id, t.toString());
        throw new CommandPersistenceException("Command lookup temporarily unavailable: " + id+" "+t);
    }
}
