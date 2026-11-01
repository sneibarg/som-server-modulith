package org.springy.som.modulith.prompt.internal;

import org.springy.som.modulith.prompt.api.PromptMapper;
import org.springy.som.modulith.prompt.api.PromptView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/agent-prompts", produces = "application/json")
public class PromptController {
    private final PromptService promptService;

    public PromptController(PromptService promptService) {
        this.promptService = promptService;
    }

    @GetMapping
    public List<PromptView> getAll() {
        return promptService.getAll().stream().map(PromptMapper::toView).toList();
    }

    @GetMapping("/{name}")
    public PromptView get(@PathVariable String name) {
        return PromptMapper.toView(promptService.get(name));
    }

    @PutMapping("/{name}")
    public PromptView put(@PathVariable String name, @RequestBody PromptDocument document) {
        return PromptMapper.toView(promptService.save(name, document));
    }
}
