package org.springy.som.modulith.agents.internal;

import org.springy.som.modulith.agents.api.AgentMapper;
import org.springy.som.modulith.agents.api.AgentView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/agents", produces = "application/json")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public List<AgentView> getAll() {
        return agentService.getAll().stream().map(AgentMapper::toView).toList();
    }

    @GetMapping("/{name}")
    public AgentView get(@PathVariable String name) {
        return AgentMapper.toView(agentService.get(name));
    }

    @PutMapping("/{name}")
    public AgentView put(@PathVariable String name, @RequestBody AgentDocument document) {
        return AgentMapper.toView(agentService.save(name, document));
    }
}
