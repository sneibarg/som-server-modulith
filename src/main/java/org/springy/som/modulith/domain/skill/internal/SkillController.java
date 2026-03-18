package org.springy.som.modulith.domain.skill.internal;

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
import org.springy.som.modulith.domain.skill.api.SkillMapper;
import org.springy.som.modulith.domain.skill.api.SkillView;
import org.springy.som.modulith.web.DeleteAllResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/skills", produces = "application/json")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<List<SkillView>> getAllSkills() {
        List<SkillView> skillViews = skillService.getAllSkills()
                .stream()
                .map(SkillMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(skillViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<SkillView> getSkillById(@PathVariable String id) {
        return ResponseEntity.ok(SkillMapper.toView(skillService.getSkillById(id)));
    }

    @PostMapping
    public ResponseEntity<SkillView> createSkill(@Valid @RequestBody SkillDocument skillDocument) {
        SkillDocument saved = skillService.createSkill(skillDocument);
        SkillView skillView = SkillMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/skills/" + saved.getId()))
                .body(skillView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillView> updateSkill(@PathVariable String id, @Valid @RequestBody SkillDocument skillDocument) {
        SkillDocument updated = skillService.saveSkillForId(id, skillDocument);
        SkillView skillView = SkillMapper.toView(updated);
        return ResponseEntity.ok(skillView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable String id) {
        skillService.deleteSkillById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(skillService.deleteAllSkills()));
    }
}
