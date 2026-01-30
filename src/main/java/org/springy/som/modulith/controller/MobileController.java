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
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.MobileService;
import org.springy.som.modulith.domain.mobile.Mobile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/mobiles", produces = "application/json")
public class MobileController {
    private final MobileService mobileService;

    public  MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }

    @GetMapping
    public ResponseEntity<List<Mobile>> getMobiles() {
        return ResponseEntity.ok(mobileService.getAllMobiles());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Mobile> getMobileById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(mobileService.getMobileById(id));
    }

    @PostMapping
    public ResponseEntity<Mobile> createMobile(@Valid @RequestBody Mobile mobile) {
        Mobile saved = mobileService.createMobile(mobile);
        return ResponseEntity
                .created(URI.create("/api/v1/characters/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mobile> updateMobile(@PathVariable String id, @Valid @RequestBody Mobile mobile) {
        Mobile updated = mobileService.saveMobileForId(id, mobile);
        return ResponseEntity.ok(updated);
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
