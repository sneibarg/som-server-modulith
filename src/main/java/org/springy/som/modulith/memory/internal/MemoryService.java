package org.springy.som.modulith.memory.internal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springy.som.modulith.memory.api.MemoryApi;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MemoryService implements MemoryApi {
    private final MemoryRepository memoryRepository;
    private final MemoryRetentionProperties retention;
    private final Clock clock;

    @Autowired
    public MemoryService(MemoryRepository memoryRepository, MemoryRetentionProperties retention) {
        this(memoryRepository, retention, Clock.systemUTC());
    }

    MemoryService(MemoryRepository memoryRepository, MemoryRetentionProperties retention, Clock clock) {
        this.memoryRepository = memoryRepository;
        this.retention = retention;
        this.clock = clock;
    }

    public MemoryDocument get(String agentName) {
        if (!StringUtils.hasText(agentName)) {
            throw new InvalidMemoryException("agentName must not be blank");
        }
        try {
            List<MemoryDocument> found = memoryRepository.findAllByAgentNameIgnoreCase(agentName);
            if (found == null || found.isEmpty()) {
                throw new MemoryNotFoundException("No memory stored for " + agentName);
            }
            Instant cutoff = clock.instant().minus(retention.retention());
            for (MemoryDocument document : found) {
                Instant savedAt = document.getSavedAt();
                if (savedAt != null && !savedAt.isBefore(cutoff)) {
                    return document;
                }
            }
            throw new MemoryNotFoundException("No memory stored for " + agentName);
        } catch (DataAccessException ex) {
            log.warn("DB failure loading memory agentName={}", agentName, ex);
            throw new MemoryPersistenceException("Failed to load memory");
        }
    }

    @Transactional
    public MemoryDocument save(String agentName, MemoryDocument document) {
        if (!StringUtils.hasText(agentName)) {
            throw new InvalidMemoryException("agentName must not be blank");
        }
        MemoryDocument stored = document == null ? new MemoryDocument() : document;
        String bodyName = stored.getAgentName();
        if (bodyName != null && !StringUtils.hasText(bodyName)) {
            throw new InvalidMemoryException("agentName must not be blank");
        }
        if (StringUtils.hasText(bodyName) && !bodyName.equalsIgnoreCase(agentName)) {
            throw new InvalidMemoryException("agentName does not match the path");
        }
        String storedName = StringUtils.hasText(bodyName) ? bodyName : agentName;
        try {
            List<MemoryDocument> existing = memoryRepository.findAllByAgentNameIgnoreCase(agentName);
            if (existing == null || existing.isEmpty()) {
                stored.setId(null);
            } else {
                stored.setId(existing.get(0).getId());
                for (int index = 1; index < existing.size(); index++) {
                    memoryRepository.deleteById(existing.get(index).getId());
                }
            }
            stored.setAgentName(storedName);
            stored.setSavedAt(clock.instant());
            normalize(stored);
            return memoryRepository.save(stored);
        } catch (DataAccessException ex) {
            log.warn("DB failure saving memory agentName={}", agentName, ex);
            throw new MemoryPersistenceException("Failed to save memory");
        }
    }

    private static void normalize(MemoryDocument document) {
        document.setPlayers(normalizeBeings(document.getPlayers()));
        document.setAgenticPlayers(normalizeBeings(document.getAgenticPlayers()));
        document.setMobiles(normalizeBeings(document.getMobiles()));
        document.setNpcs(normalizeBeings(document.getNpcs()));
        document.setAggressiveMobiles(normalizeBeings(document.getAggressiveMobiles()));
        document.setRooms(emptyIfNull(document.getRooms()));
        document.setCommands(emptyIfNull(document.getCommands()));
        document.setConsiders(emptyIfNull(document.getConsiders()));
        document.setConversations(emptyIfNull(document.getConversations()));
        for (MemoryDocument.Room room : document.getRooms()) {
            if (room != null) {
                room.setExits(emptyIfNull(room.getExits()));
            }
        }
        for (MemoryDocument.Conversation conversation : document.getConversations()) {
            if (conversation != null) {
                conversation.setParticipants(emptyIfNull(conversation.getParticipants()));
            }
        }
    }

    private static List<MemoryDocument.Being> normalizeBeings(List<MemoryDocument.Being> beings) {
        List<MemoryDocument.Being> stored = emptyIfNull(beings);
        for (MemoryDocument.Being being : stored) {
            if (being != null) {
                being.setAuras(emptyIfNull(being.getAuras()));
            }
        }
        return stored;
    }

    private static <T> List<T> emptyIfNull(List<T> values) {
        return values == null ? new ArrayList<>() : values;
    }
}
