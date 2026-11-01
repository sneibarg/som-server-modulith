package org.springy.som.modulith.knowledge.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springy.som.modulith.knowledge.api.KnowledgeApi;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KnowledgeService implements KnowledgeApi {
    private final KnowledgeRepository knowledgeRepository;

    public KnowledgeService(KnowledgeRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    public KnowledgeDocument get(String characterId) {
        if (!StringUtils.hasText(characterId)) {
            throw new InvalidKnowledgeException("characterId must not be blank");
        }
        try {
            return knowledgeRepository.findById(characterId)
                    .orElseThrow(() -> new KnowledgeNotFoundException("No knowledge stored for " + characterId));
        } catch (DataAccessException ex) {
            log.warn("DB failure loading knowledge characterId={}", characterId, ex);
            throw new KnowledgePersistenceException("Failed to load knowledge");
        }
    }

    public KnowledgeDocument save(String characterId, KnowledgeDocument document) {
        if (!StringUtils.hasText(characterId)) {
            throw new InvalidKnowledgeException("characterId must not be blank");
        }
        KnowledgeDocument stored = document == null ? new KnowledgeDocument() : document;
        String bodyId = stored.getCharacterId();
        if (StringUtils.hasText(bodyId) && !bodyId.equals(characterId)) {
            throw new InvalidKnowledgeException("characterId does not match the path");
        }
        stored.setCharacterId(characterId);
        normalize(stored);
        try {
            return knowledgeRepository.save(stored);
        } catch (DataAccessException ex) {
            log.warn("DB failure saving knowledge characterId={}", characterId, ex);
            throw new KnowledgePersistenceException("Failed to save knowledge");
        }
    }

    private static void normalize(KnowledgeDocument document) {
        document.setLoginLines(emptyIfNull(document.getLoginLines()));
        document.setHelpTopics(emptyIfNull(document.getHelpTopics()));
        document.setAreas(emptyIfNull(document.getAreas()));
        document.setMobiles(emptyIfNull(document.getMobiles()));
        document.setNpcs(emptyIfNull(document.getNpcs()));
        document.setPlayers(emptyIfNull(document.getPlayers()));
        for (KnowledgeDocument.HelpTopic topic : document.getHelpTopics()) {
            if (topic != null) {
                topic.setTopics(emptyIfNull(topic.getTopics()));
            }
        }
        for (KnowledgeDocument.Being being : document.getMobiles()) {
            if (being != null) {
                being.setAuras(emptyIfNull(being.getAuras()));
            }
        }
        for (KnowledgeDocument.Being being : document.getNpcs()) {
            if (being != null) {
                being.setAuras(emptyIfNull(being.getAuras()));
            }
        }
        for (KnowledgeDocument.Player player : document.getPlayers()) {
            if (player != null) {
                player.setAuras(emptyIfNull(player.getAuras()));
            }
        }
        KnowledgeDocument.Room room = document.getRoom();
        if (room != null) {
            room.setExits(emptyIfNull(room.getExits()));
            room.setItemLines(emptyIfNull(room.getItemLines()));
        }
    }

    private static <T> List<T> emptyIfNull(List<T> values) {
        return values == null ? new ArrayList<>() : values;
    }
}
