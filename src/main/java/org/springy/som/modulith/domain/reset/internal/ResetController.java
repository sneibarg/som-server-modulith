package org.springy.som.modulith.domain.reset.internal;

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
import org.springy.som.modulith.domain.reset.api.ResetMapper;
import org.springy.som.modulith.domain.reset.api.ResetView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/resets", produces = "application/json")
public class ResetController {
    private final ResetService resetService;

    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    @GetMapping
    public ResponseEntity<List<ResetView>> getResets() {
        List<ResetView> resetViews = new ArrayList<>();
        for (ResetDocument resetDocument : resetService.getAllResets())
            resetViews.add(ResetMapper.toView(resetDocument));
        return ResponseEntity.ok(resetViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResetView> getResetById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(ResetMapper.toView(resetService.getResetById(id)));
    }

    @PostMapping
    public ResponseEntity<ResetView> createReset(@Valid @RequestBody ResetDocument resetDocument) {
        ResetDocument saved = resetService.createReset(resetDocument);
        ResetView resetView = ResetMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/resets/" + saved.getId()))
                .body(resetView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResetView> updateReset(@PathVariable String id, @Valid @RequestBody ResetDocument resetDocument) {
        ResetDocument updated = resetService.saveResetForId(id, resetDocument);
        ResetView resetView = ResetMapper.toView(updated);
        return ResponseEntity.ok(resetView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResetsById(@PathVariable String id) {
        resetService.deleteResetById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(resetService.deleteAllResets()));
    }
}
