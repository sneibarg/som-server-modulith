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
import org.springy.som.modulith.service.ClassService;
import org.springy.som.modulith.domain.clazz.RomClass;
import org.springy.som.modulith.service.DeleteAllResponse;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/classes", produces = "application/json")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<RomClass>> getAllRomClasses() {
        return ResponseEntity.ok(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RomClass> getRomClassById(@PathVariable String id) {
        return ResponseEntity.ok(classService.getRomClassById(id));
    }

    @PostMapping
    public ResponseEntity<RomClass> createRomClass(@Valid @RequestBody RomClass romClass) {
        RomClass saved = classService.createRomClass(romClass);
        return ResponseEntity
                .created(URI.create("/api/v1/classes/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RomClass> updateArea(@PathVariable String id, @Valid @RequestBody RomClass romClass) {
        RomClass updated = classService.saveRomClassForId(id, romClass);
        return ResponseEntity.ok(updated);
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
