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
import org.springy.som.modulith.domain.command.api.HelpMapper;
import org.springy.som.modulith.domain.command.api.HelpView;
import org.springy.som.modulith.web.DeleteAllResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/helps", produces = "application/json")
public class HelpController {
    private final HelpService helpService;
    public HelpController(HelpService helpService) {
        this.helpService = helpService;
    }

    @GetMapping
    public ResponseEntity<List<HelpView>> getHelps() {
        List<HelpView> helpViews = helpService.getAllHelps()
                .stream()
                .map(HelpMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(helpViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<HelpView> getHelpById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(HelpMapper.toView(helpService.getHelpById(id)));
    }

    @GetMapping(path = "/keyword/{keyword}")
    public ResponseEntity<HelpView> getHelpByKeyword(@PathVariable String keyword) {
        return ResponseEntity.ok(HelpMapper.toView(helpService.getHelpByKeyword(keyword)));
    }

    @PostMapping
    public ResponseEntity<HelpView> createHelp(@Valid @RequestBody HelpDocument helpDocument) {
        HelpDocument saved = helpService.createHelp(helpDocument);
        HelpView helpView = HelpMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/helps/" + saved.getId()))
                .body(helpView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HelpView> updateHelp(@PathVariable String id, @Valid @RequestBody HelpDocument helpDocument) {
        HelpDocument updated = helpService.saveHelpForId(id, helpDocument);
        HelpView helpView = HelpMapper.toView(updated);
        return ResponseEntity.ok(helpView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHelpById(@PathVariable String id) {
        helpService.deleteHelpById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(helpService.deleteAllHelps()));
    }
}
