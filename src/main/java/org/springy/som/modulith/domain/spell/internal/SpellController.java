package org.springy.som.modulith.domain.spell.internal;

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
import org.springy.som.modulith.domain.spell.api.SpellMapper;
import org.springy.som.modulith.domain.spell.api.SpellView;
import org.springy.som.modulith.web.DeleteAllResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/spells", produces = "application/json")
public class SpellController {
    private final SpellService spellService;

    public SpellController(SpellService spellService) {
        this.spellService = spellService;
    }

    @GetMapping
    public ResponseEntity<List<SpellView>> getAllSpells() {
        List<SpellView> spellViews = spellService.getAllSpells()
                .stream()
                .map(SpellMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(spellViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SpellView> getSpellById(@PathVariable String id) {
        return ResponseEntity.ok(SpellMapper.toView(spellService.getSpellById(id)));
    }

    @PostMapping
    public ResponseEntity<SpellView> createSpell(@Valid @RequestBody SpellDocument spellDocument) {
        SpellDocument saved = spellService.createSpell(spellDocument);
        SpellView spellView = SpellMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/spells/" + saved.getId()))
                .body(spellView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpellView> updateSpell(@PathVariable String id, @Valid @RequestBody SpellDocument spellDocument) {
        SpellDocument updated = spellService.saveSpellForId(id, spellDocument);
        SpellView spellView = SpellMapper.toView(updated);
        return ResponseEntity.ok(spellView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpell(@PathVariable String id) {
        spellService.deleteSpellById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(spellService.deleteAllSpells()));
    }
}
