package org.springy.som.modulith.domain.command.internal;

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
import org.springy.som.modulith.web.DeleteAllResponse;
import org.springy.som.modulith.domain.command.api.CommandMapper;
import org.springy.som.modulith.domain.command.api.CommandView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/commands", produces = "application/json")
public class CommandController {
    private final CommandService commandService;
    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @GetMapping
    public ResponseEntity<List<CommandView>> getCommands() {
        List<CommandView> commandViews = new ArrayList<>();
        for (CommandDocument commandDocument : commandService.getAllCommands())
            commandViews.add(CommandMapper.toView(commandDocument));
        return ResponseEntity.ok(commandViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CommandView> getCommandById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(CommandMapper.toView(commandService.getCommandById(id)));
    }

    @PostMapping
    public ResponseEntity<CommandView> createCommand(@Valid @RequestBody CommandDocument commandDocument) {
        CommandDocument saved = commandService.createCommand(commandDocument);
        CommandView commandView = CommandMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(commandView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommandView> updateCommand(@PathVariable String id, @Valid @RequestBody CommandDocument commandDocument) {
        CommandDocument updated = commandService.saveCommandForId(id, commandDocument);
        CommandView commandView = CommandMapper.toView(updated);
        return ResponseEntity.ok(commandView);
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
