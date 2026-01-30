package org.springy.som.modulith.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.CommandService;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.service.DeleteAllResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/commands", produces = "application/json")
public class CommandController {
    private final CommandService commandService;
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity<List<Command>> getCommands() {
        return ResponseEntity.ok(commandService.getAllCommands());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Command> getCommandById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(commandService.getCommandById(id));
    }

    @PostMapping
    public ResponseEntity<Command> createCommand(@Valid @RequestBody Command command) {
        Command saved = commandService.createCommand(command);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Command> updateCommand(@PathVariable String id, @Valid @RequestBody Command command) {
        Command updated = commandService.saveCommandForId(id, command);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommandById(@PathVariable String id) {
        commandService.deleteCommandById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(commandService.deleteAllCommands()));
    }
}
