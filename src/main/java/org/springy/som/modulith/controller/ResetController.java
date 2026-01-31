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
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.ResetService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/resets", produces = "application/json")
public class ResetController {
    private final ResetService resetService;

    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    @GetMapping
    public ResponseEntity<List<Reset>> getResets() {
        return ResponseEntity.ok(resetService.getAllResets());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Reset> getResetById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(resetService.getResetById(id));
    }

    @PostMapping
    public ResponseEntity<Reset> createReset(@Valid @RequestBody Reset reset) {
        Reset saved = resetService.createReset(reset);
        return ResponseEntity
                .created(URI.create("/api/v1/resets/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reset> updateReset(@PathVariable String id, @Valid @RequestBody Reset reset) {
        Reset updated = resetService.saveResetForId(id, reset);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResetsById(@PathVariable String id) {
        resetService.deleteResetById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(resetService.deleteAllResets()));
    }
}
