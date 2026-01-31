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
import org.springy.som.modulith.service.RaceService;
import org.springy.som.modulith.domain.race.RomRace;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/races", produces = "application/json")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    public ResponseEntity<List<RomRace>> getRaces() {
        return ResponseEntity.ok(raceService.getAllRomRaces());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RomRace> getRaceById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(raceService.getRomRaceById(id));
    }

    @PostMapping
    public ResponseEntity<RomRace> createRomRace(@Valid @RequestBody RomRace romRace) {
        RomRace saved = raceService.createRomRace(romRace);
        return ResponseEntity
                .created(URI.create("/api/v1/races/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RomRace> updateMobile(@PathVariable String id, @Valid @RequestBody RomRace romRace) {
        RomRace updated = raceService.saveRomRaceForId(id, romRace);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMobileById(@PathVariable String id) {
        raceService.deleteRomRaceById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(raceService.deleteAllRomRace()));
    }
}
