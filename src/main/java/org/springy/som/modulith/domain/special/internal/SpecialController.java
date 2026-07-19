package org.springy.som.modulith.domain.special.internal;

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
import org.springy.som.modulith.domain.special.api.SpecialMapper;
import org.springy.som.modulith.domain.special.api.SpecialView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/specials", produces = "application/json")
public class SpecialController {
    private final SpecialService specialService;

    public SpecialController(SpecialService specialService) {
        this.specialService = specialService;
    }

    @GetMapping
    public ResponseEntity<List<SpecialView>> getSpecials() {
        List<SpecialView> specialViews = specialService.getAllSpecials()
                .stream()
                .map(SpecialMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(specialViews);
    }

    @GetMapping(path = "/area/{id}")
    public ResponseEntity<List<SpecialView>> getSpecialsByAreaId(@Valid @PathVariable String id) {
        List<SpecialView> specialViews = specialService.getSpecialsByAreaId(id)
                .stream()
                .map(SpecialMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(specialViews);
    }

    @GetMapping(path = "/mob/{vnum}")
    public ResponseEntity<List<SpecialView>> getSpecialsByMobVnum(@Valid @PathVariable String vnum) {
        List<SpecialView> specialViews = specialService.getSpecialsByMobVnum(vnum)
                .stream()
                .map(SpecialMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(specialViews);    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SpecialView> getSpecialById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(SpecialMapper.toView(specialService.getSpecialById(id)));
    }

    @PostMapping
    public ResponseEntity<SpecialView> createSpecial(@Valid @RequestBody SpecialDocument specialDocument) {
        SpecialDocument saved = specialService.createSpecial(specialDocument);
        SpecialView updated = SpecialMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/Specials/" + saved.getId()))
                .body(updated);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpecialView> updateSpecial(@PathVariable String id, @Valid @RequestBody SpecialDocument specialDocument) {
        SpecialDocument updated = specialService.saveSpecialForId(id, specialDocument);
        SpecialView updatedView = SpecialMapper.toView(updated);
        return ResponseEntity.ok(updatedView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecialsById(@PathVariable String id) {
        specialService.deleteSpecialById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(specialService.deleteAllSpecials()));
    }
}
