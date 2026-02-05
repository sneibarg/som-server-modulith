package org.springy.som.modulith.domain.clazz.internal;

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
import org.springy.som.modulith.domain.clazz.api.RomClassMapper;
import org.springy.som.modulith.domain.clazz.api.RomClassView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/classes", produces = "application/json")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<RomClassView>> getAllRomClasses() {
        List<RomClassView> romClassViews = new ArrayList<>();
        for (RomClassDocument romClassDocument : classService.getAllClasses())
            romClassViews.add(RomClassMapper.toView(romClassDocument));
        return ResponseEntity.ok(romClassViews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RomClassView> getRomClassById(@PathVariable String id) {
        return ResponseEntity.ok(RomClassMapper.toView(classService.getRomClassById(id)));
    }

    @PostMapping
    public ResponseEntity<RomClassView> createRomClass(@Valid @RequestBody RomClassDocument romClassDocument) {
        RomClassDocument saved = classService.createRomClass(romClassDocument);
        RomClassView romClassView = RomClassMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/classes/" + saved.getId()))
                .body(romClassView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RomClassView> updateArea(@PathVariable String id, @Valid @RequestBody RomClassDocument romClassDocument) {
        RomClassDocument updated = classService.saveRomClassForId(id, romClassDocument);
        RomClassView romClassView = RomClassMapper.toView(updated);
        return ResponseEntity.ok(romClassView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArea(@PathVariable String id) {
        classService.deleteRomClassById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(classService.deleteAllRomClasses()));
    }
}
