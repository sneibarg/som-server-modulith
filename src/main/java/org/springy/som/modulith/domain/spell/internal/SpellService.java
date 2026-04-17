package org.springy.som.modulith.domain.spell.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.DomainGuards;
import org.springy.som.modulith.domain.spell.api.SpellApi;
import org.springy.som.modulith.domain.spell.api.SpellDeletedEvent;

import java.util.List;

import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class SpellService implements SpellApi {
    private final SpellRepository spellRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SpellService(SpellRepository spellRepository, ApplicationEventPublisher eventPublisher) {
        this.spellRepository = spellRepository;
        this.eventPublisher = eventPublisher;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllSpellsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<SpellDocument> getAllSpells() {
        try {
            return spellRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAllSpells", ex);
            throw new SpellPersistenceException("Failed to load Spells "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getSpellByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpellDocument getSpellById(String id) {
        requireText(id, DomainGuards.spellIdMissing());

        try {
            SpellDocument spellDocument = spellRepository.findSpellById(id);
            if (spellDocument == null) {
                throw new SpellNotFoundException(id);
            }
            return spellDocument;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getSpellById id={}", id, ex);
            throw new SpellPersistenceException("Failed to load Spell: " + id + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getSpellByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpellDocument getSpellByName(String name) {
        requireText(name, DomainGuards.spellNameMissing());

        try {
            SpellDocument spellDocument = spellRepository.findSpellByName(name);
            if (spellDocument == null) {
                throw new SpellNotFoundException(name);
            }
            return spellDocument;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getSpellById id={}", name, ex);
            throw new SpellPersistenceException("Failed to load Spell: " + name + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpellDocument createSpell(SpellDocument spellDocument) {
        requireEntityWithId(spellDocument, SpellDocument::getId, DomainGuards.spellIdMissing(), DomainGuards.spellIdMissing());

        try {
            // if (SkillRepository.existsById(SpellDocument.getSpellId())) throw new SpellConflictException(...)
            return spellRepository.save(spellDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createSpell SpellId={}", safeId(spellDocument, SpellDocument::getId), ex);
            throw new SpellPersistenceException("Failed to create SpellDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpellDocument saveSpellForId(String id, SpellDocument spellDocument) {
        SpellDocument existing = spellRepository.findSpellById(id);
        requireText(existing.getId(), DomainGuards.skillIdMissing());
        requireEntityWithId(existing, SpellDocument::getId, DomainGuards.skillMissing(), DomainGuards.skillIdMissing());

        return spellRepository.save(spellDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteSpellById(String id) {
        requireText(id, DomainGuards.spellIdMissing());

        try {
            if (!spellRepository.existsById(id)) {
                throw new SpellNotFoundException(id);
            }
            spellRepository.deleteById(id);
            eventPublisher.publishEvent(new SpellDeletedEvent(id));
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteSpellById id={}", id, ex);
            throw new SpellPersistenceException("Failed to delete Spell: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllSpells() {
        try {
            List<String> spellIds = spellRepository.findAll()
                    .stream()
                    .map(SpellDocument::getId)
                    .toList();
            long itemCount = spellIds.size();
            spellRepository.deleteAll();
            for (String spellId : spellIds) {
                eventPublisher.publishEvent(new SpellDeletedEvent(spellId));
            }
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllSpells", ex);
            throw new SpellPersistenceException("Failed to delete all Spells "+ ex);
        }
    }

    private List<SpellDocument> getAllSpellsFallback(Throwable t) {
        log.warn("Fallback getAllSpells due to {}", t.toString());
        return List.of();
    }

    private SpellDocument getSpellByIdFallback(String id, Throwable t) {
        log.warn("Fallback getSpellById id={} due to {}", id, t.toString());
        throw new SpellPersistenceException("SpellDocument lookup temporarily unavailable: " + id+" "+t);
    }
}
