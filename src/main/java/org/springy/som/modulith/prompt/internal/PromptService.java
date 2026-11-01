package org.springy.som.modulith.prompt.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springy.som.modulith.prompt.api.PromptApi;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PromptService implements PromptApi {
    private final PromptRepository promptRepository;

    public PromptService(PromptRepository promptRepository) {
        this.promptRepository = promptRepository;
    }

    @Override
    public List<PromptDocument> getAll() {
        try {
            return promptRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure loading prompts", ex);
            throw new PromptPersistenceException("Failed to load prompts");
        }
    }

    @Override
    public PromptDocument get(String name) {
        String promptName = requiredName(name);
        try {
            List<PromptDocument> found = promptRepository.findAllByNameIgnoreCase(promptName);
            if (found == null || found.isEmpty()) {
                throw new PromptNotFoundException("No prompt stored for " + promptName);
            }
            return found.get(0);
        } catch (DataAccessException ex) {
            log.warn("DB failure loading prompt name={}", promptName, ex);
            throw new PromptPersistenceException("Failed to load prompt");
        }
    }

    @Override
    @Transactional
    public PromptDocument save(String name, PromptDocument document) {
        String pathName = requiredName(name);
        PromptDocument stored = document == null ? new PromptDocument() : document;
        String bodyName = stored.getName();
        if (bodyName != null && !StringUtils.hasText(bodyName)) {
            throw new InvalidPromptException("name must not be blank");
        }
        if (StringUtils.hasText(bodyName) && !bodyName.strip().equalsIgnoreCase(pathName)) {
            throw new InvalidPromptException("name does not match the path");
        }
        if (!StringUtils.hasText(stored.getModel())) {
            throw new InvalidPromptException("Prompt document requires name and model");
        }
        if (stored.getJsonSchema() == null) {
            throw new InvalidPromptException("jsonSchema must be an object");
        }
        if (stored.getTokens() == null) {
            throw new InvalidPromptException("tokens must be an object");
        }
        String storedName = StringUtils.hasText(bodyName) ? bodyName.strip() : pathName;
        validateTokens(storedName, stored.getTokens());
        stored.setName(storedName);
        stored.setModel(stored.getModel().strip());
        if (stored.getSystem() == null) {
            stored.setSystem("");
        }
        if (stored.getUser() == null) {
            stored.setUser("");
        }
        try {
            List<PromptDocument> existing = promptRepository.findAllByNameIgnoreCase(pathName);
            if (existing == null || existing.isEmpty()) {
                stored.setId(null);
            } else {
                stored.setId(existing.get(0).getId());
                for (int index = 1; index < existing.size(); index++) {
                    promptRepository.deleteById(existing.get(index).getId());
                }
            }
            return promptRepository.save(stored);
        } catch (DataAccessException ex) {
            log.warn("DB failure saving prompt name={}", pathName, ex);
            throw new PromptPersistenceException("Failed to save prompt");
        }
    }

    private static String requiredName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidPromptException("name must not be blank");
        }
        return name.strip();
    }

    private static void validateTokens(String name, Map<String, Map<String, Object>> tokens) {
        for (Map.Entry<String, Map<String, Object>> entry : tokens.entrySet()) {
            Map<String, Object> spec = entry.getValue();
            if (spec == null || !spec.containsKey("example")) {
                throw new InvalidPromptException("Prompt '" + name + "' token '" + entry.getKey() + "' requires an example");
            }
            Object text = spec.get("text");
            if (!(text instanceof String) || !StringUtils.hasText((String) text)) {
                throw new InvalidPromptException("Prompt '" + name + "' token '" + entry.getKey() + "' requires text");
            }
        }
    }
}
