package org.springy.som.modulith.domain.area.internal;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springy.som.modulith.domain.DeleteAllResponse;
import org.springy.som.modulith.domain.area.api.AreaMapper;
import org.springy.som.modulith.domain.area.api.AreaView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/areas", produces = "application/json")
public class AreaController {
    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public ResponseEntity<List<AreaView>> getAllAreas() {
        List<AreaView> areaViews = new ArrayList<>();
        for (AreaDocument area : areaService.getAllAreas())
            areaViews.add(AreaMapper.toView(area));
        return ResponseEntity.ok(areaViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<AreaView> getAreaById(@PathVariable String id) {
        return ResponseEntity.ok(AreaMapper.toView(areaService.getAreaById(id)));
    }

    @PostMapping
    public ResponseEntity<AreaView> createArea(@Valid @RequestBody AreaDocument areaDocument) {
        AreaDocument saved = areaService.createArea(areaDocument);
        AreaView areaView = AreaMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/areas/" + saved.getId()))
                .body(areaView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AreaView> updateArea(@PathVariable String id, @Valid @RequestBody AreaDocument areaDocument) {
        AreaDocument updated = areaService.saveAreaForId(id, areaDocument);
        AreaView areaView = AreaMapper.toView(updated);
        return ResponseEntity.ok(areaView);
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
