package org.springy.som.modulith.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.service.AreaService;
import org.springframework.http.ResponseEntity;
import org.springy.som.modulith.service.DeleteAllResponse;


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/areas", produces = "application/json")
public class AreaController {
    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<List<Area>> getAllAreas() {
        return ResponseEntity.ok(areaService.getAllAreas());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Area> getAreaById(@PathVariable String id) {
        return ResponseEntity.ok(areaService.getAreaById(id));
    }

    @PostMapping
    public ResponseEntity<Area> createArea(@Valid @RequestBody Area area) {
        Area saved = areaService.createArea(area);
        return ResponseEntity
                .created(URI.create("/api/v1/areas/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable String id, @Valid @RequestBody Area area) {
        Area updated = areaService.saveAreaForId(id, area);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable String id) {
        areaService.deleteAreaById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(areaService.deleteAllAreas()));
    }
}
