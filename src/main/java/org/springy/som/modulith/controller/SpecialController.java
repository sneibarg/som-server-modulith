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
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.SpecialService;
import org.springy.som.modulith.domain.special.Special;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/specials", produces = "application/json")
public class SpecialController {
    private final SpecialService specialService;

    public SpecialController(SpecialService specialService) {
        this.specialService = specialService;
    }

    @GetMapping
    public ResponseEntity<List<Special>> getResets() {
        return ResponseEntity.ok(specialService.getAllSpecials());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Special> getResetById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(specialService.getSpecialById(id));
    }

    @PostMapping
    public ResponseEntity<Special> createReset(@Valid @RequestBody Special special) {
        Special saved = specialService.createSpecial(special);
        return ResponseEntity
                .created(URI.create("/api/v1/resets/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Special> updateReset(@PathVariable String id, @Valid @RequestBody Special special) {
        Special updated = specialService.saveSpecialForId(id, special);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResetsById(@PathVariable String id) {
        specialService.deleteSpecialById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(specialService.deleteAllSpecials()));
    }
}
