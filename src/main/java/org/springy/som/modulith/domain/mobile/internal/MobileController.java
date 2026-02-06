package org.springy.som.modulith.domain.mobile.internal;

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
import org.springy.som.modulith.domain.mobile.api.MobileMapper;
import org.springy.som.modulith.domain.mobile.api.MobileView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/mobiles", produces = "application/json")
public class MobileController {
    private final MobileService mobileService;

    public  MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @GetMapping
    public ResponseEntity<List<MobileView>> getMobiles() {
        List<MobileView> mobileViews = mobileService.getAllMobiles()
                .stream()
                .map(MobileMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mobileViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MobileView> getMobileById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(MobileMapper.toView(mobileService.getMobileById(id)));
    }

    @PostMapping
    public ResponseEntity<MobileView> createMobile(@Valid @RequestBody MobileDocument mobileDocument) {
        MobileDocument saved = mobileService.createMobile(mobileDocument);
        MobileView mobileView = MobileMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(mobileView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MobileView> updateMobile(@PathVariable String id, @Valid @RequestBody MobileDocument mobileDocument) {
        MobileDocument updated = mobileService.saveMobileForId(id, mobileDocument);
        MobileView mobileView = MobileMapper.toView(updated);
        return ResponseEntity.ok(mobileView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMobileById(@PathVariable String id) {
        mobileService.deleteMobileById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(mobileService.deleteAllMobiles()));
    }
}
