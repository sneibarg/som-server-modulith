package org.springy.som.modulith.domain.race.internal;

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
import org.springy.som.modulith.domain.DeleteAllResponse;
import org.springy.som.modulith.domain.race.api.RomRaceMapper;
import org.springy.som.modulith.domain.race.api.RomRaceView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/races", produces = "application/json")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    public ResponseEntity<List<RomRaceView>> getRaces() {
        List<RomRaceView> romRaceViews = new ArrayList<>();
        for (RomRaceDocument romRaceDocument : raceService.getAllRomRaces())
            romRaceViews.add(RomRaceMapper.toView(romRaceDocument));
        return ResponseEntity.ok(romRaceViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RomRaceView> getRaceById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(RomRaceMapper.toView(raceService.getRomRaceById(id)));
    }

    @PostMapping
    public ResponseEntity<RomRaceView> createRomRace(@Valid @RequestBody RomRaceDocument romRaceDocument) {
        RomRaceDocument saved = raceService.createRomRace(romRaceDocument);
        RomRaceView romRaceView = RomRaceMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/races/" + saved.getId()))
                .body(romRaceView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RomRaceView> updateMobile(@PathVariable String id, @Valid @RequestBody RomRaceDocument romRaceDocument) {
        RomRaceDocument updated = raceService.saveRomRaceForId(id, romRaceDocument);
        RomRaceView romRaceView = RomRaceMapper.toView(updated);
        return ResponseEntity.ok(romRaceView);
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
