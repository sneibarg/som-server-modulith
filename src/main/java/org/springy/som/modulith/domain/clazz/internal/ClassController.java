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
import org.springy.som.modulith.web.DeleteAllResponse;
import org.springy.som.modulith.domain.clazz.api.ClassMapper;
import org.springy.som.modulith.domain.clazz.api.ClassView;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/classes", produces = "application/json")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public ResponseEntity<List<ClassView>> getAllRomClasses() {
        List<ClassView> classViews = classService.getAllClasses()
                .stream()
                .map(ClassMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(classViews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassView> getRomClassById(@PathVariable String id) {
        return ResponseEntity.ok(ClassMapper.toView(classService.getRomClassById(id)));
    }

    @PostMapping
    public ResponseEntity<ClassView> createRomClass(@Valid @RequestBody ClassDocument classDocument) {
        ClassDocument saved = classService.createRomClass(classDocument);
        ClassView classView = ClassMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/classes/" + saved.getId()))
                .body(classView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassView> updateArea(@PathVariable String id, @Valid @RequestBody ClassDocument classDocument) {
        ClassDocument updated = classService.saveRomClassForId(id, classDocument);
        ClassView classView = ClassMapper.toView(updated);
        return ResponseEntity.ok(classView);
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
