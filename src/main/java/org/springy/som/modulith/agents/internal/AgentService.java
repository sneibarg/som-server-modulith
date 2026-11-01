package org.springy.som.modulith.agents.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springy.som.modulith.agents.api.AgentApi;

import java.util.List;

@Slf4j
@Service
public class AgentService implements AgentApi {
    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public List<AgentDocument> getAll() {
        try {
            return agentRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure loading agents", ex);
            throw new AgentPersistenceException("Failed to load agents");
        }
    }

    @Override
    public AgentDocument get(String name) {
        String agentName = requiredName(name);
        try {
            List<AgentDocument> found = agentRepository.findAllByNameIgnoreCase(agentName);
            if (found == null || found.isEmpty()) {
                throw new AgentNotFoundException("No agent stored for " + agentName);
            }
            return found.get(0);
        } catch (DataAccessException ex) {
            log.warn("DB failure loading agent name={}", agentName, ex);
            throw new AgentPersistenceException("Failed to load agent");
        }
    }

    @Override
    @Transactional
    public AgentDocument save(String name, AgentDocument document) {
        String pathName = requiredName(name);
        AgentDocument stored = document == null ? new AgentDocument() : document;
        String bodyName = stored.getName();
        if (bodyName != null && !StringUtils.hasText(bodyName)) {
            throw new InvalidAgentException("name must not be blank");
        }
        if (StringUtils.hasText(bodyName) && !bodyName.strip().equalsIgnoreCase(pathName)) {
            throw new InvalidAgentException("name does not match the path");
        }
        if (!StringUtils.hasText(stored.getAccountName()) || !StringUtils.hasText(stored.getCharacterName())) {
            throw new InvalidAgentException("Agent document requires name, accountName, and characterName");
        }
        String storedName = StringUtils.hasText(bodyName) ? bodyName.strip() : pathName;
        stored.setName(storedName);
        stored.setAccountName(stored.getAccountName().strip());
        stored.setCharacterName(stored.getCharacterName().strip());
        if (stored.getEnabled() == null) {
            stored.setEnabled(true);
        }
        if (stored.getDescription() == null) {
            stored.setDescription("");
        }
        try {
            List<AgentDocument> existing = agentRepository.findAllByNameIgnoreCase(pathName);
            if (existing == null || existing.isEmpty()) {
                stored.setId(StringUtils.hasText(stored.getId()) ? stored.getId().strip() : null);
            } else {
                stored.setId(existing.get(0).getId());
                for (int index = 1; index < existing.size(); index++) {
                    agentRepository.deleteById(existing.get(index).getId());
                }
            }
            return agentRepository.save(stored);
        } catch (DataAccessException ex) {
            log.warn("DB failure saving agent name={}", pathName, ex);
            throw new AgentPersistenceException("Failed to save agent");
        }
    }

    private static String requiredName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidAgentException("name must not be blank");
        }
        return name.strip();
    }
}
