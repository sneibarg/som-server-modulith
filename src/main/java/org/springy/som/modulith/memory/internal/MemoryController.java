package org.springy.som.modulith.memory.internal;

import org.springy.som.modulith.memory.api.MemoryMapper;
import org.springy.som.modulith.memory.api.MemoryView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/agent-memory", produces = "application/json")
public class MemoryController {
    private final MemoryService memoryService;

    public MemoryController(MemoryService memoryService) {
        this.memoryService = memoryService;
    }

    @GetMapping("/{agentName}")
    public MemoryView get(@PathVariable String agentName) {
        return MemoryMapper.toView(memoryService.get(agentName));
    }

    @PutMapping("/{agentName}")
    public MemoryView put(@PathVariable String agentName, @RequestBody MemoryDocument document) {
        return MemoryMapper.toView(memoryService.save(agentName, document));
    }
}
