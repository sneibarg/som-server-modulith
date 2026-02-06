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
import org.springy.som.modulith.web.DeleteAllResponse;
import org.springy.som.modulith.domain.race.api.RaceMapper;
import org.springy.som.modulith.domain.race.api.RaceView;

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
    public ResponseEntity<List<RaceView>> getRaces() {
        List<RaceView> raceViews = new ArrayList<>();
        for (RaceDocument raceDocument : raceService.getAllRaces())
            raceViews.add(RaceMapper.toView(raceDocument));
        return ResponseEntity.ok(raceViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RaceView> getRaceById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(RaceMapper.toView(raceService.getRaceById(id)));
    }

    @PostMapping
    public ResponseEntity<RaceView> createRomRace(@Valid @RequestBody RaceDocument raceDocument) {
        RaceDocument saved = raceService.createRace(raceDocument);
        RaceView raceView = RaceMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/races/" + saved.getId()))
                .body(raceView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaceView> updateMobile(@PathVariable String id, @Valid @RequestBody RaceDocument raceDocument) {
        RaceDocument updated = raceService.saveRaceForId(id, raceDocument);
        RaceView raceView = RaceMapper.toView(updated);
        return ResponseEntity.ok(raceView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMobileById(@PathVariable String id) {
        raceService.deleteRaceById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(raceService.deleteAllRaces()));
    }
}
