package org.springy.som.modulith.domain.command.internal;

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
import org.springy.som.modulith.domain.command.api.SocialMapper;
import org.springy.som.modulith.domain.command.api.SocialView;
import org.springy.som.modulith.web.DeleteAllResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/socials", produces = "application/json")
public class SocialController {
    private final SocialService socialService;
    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @GetMapping
    public ResponseEntity<List<SocialView>> getSocials() {
        List<SocialView> socialViews = socialService.getAllSocials()
                .stream()
                .map(SocialMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(socialViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SocialView> getSocialById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(SocialMapper.toView(socialService.getSocialById(id)));
    }

    @GetMapping(path = "/name/{name}")
    public ResponseEntity<SocialView> getSocialByName(@PathVariable String name) {
        return ResponseEntity.ok(SocialMapper.toView(socialService.getSocialByName(name)));
    }

    @PostMapping
    public ResponseEntity<SocialView> createSocial(@Valid @RequestBody SocialDocument socialDocument) {
        SocialDocument saved = socialService.createSocial(socialDocument);
        SocialView socialView = SocialMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/socials/" + saved.getId()))
                .body(socialView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocialView> updateSocial(@PathVariable String id, @Valid @RequestBody SocialDocument socialDocument) {
        SocialDocument updated = socialService.saveSocialForId(id, socialDocument);
        SocialView socialView = SocialMapper.toView(updated);
        return ResponseEntity.ok(socialView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialById(@PathVariable String id) {
        socialService.deleteSocialById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(socialService.deleteAllSocials()));
    }
}
