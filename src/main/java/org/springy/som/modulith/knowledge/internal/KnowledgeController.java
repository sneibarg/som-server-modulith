package org.springy.som.modulith.knowledge.internal;

import org.springy.som.modulith.knowledge.api.KnowledgeMapper;
import org.springy.som.modulith.knowledge.api.KnowledgeView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/agent-knowledge", produces = "application/json")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/{characterId}")
    public KnowledgeView get(@PathVariable String characterId) {
        return KnowledgeMapper.toView(knowledgeService.get(characterId));
    }

    @PutMapping("/{characterId}")
    public KnowledgeView put(@PathVariable String characterId, @RequestBody KnowledgeDocument document) {
        return KnowledgeMapper.toView(knowledgeService.save(characterId, document));
    }
}
