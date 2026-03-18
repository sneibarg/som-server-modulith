package org.springy.som.modulith.domain.skill.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.DomainGuards;
import org.springy.som.modulith.domain.skill.api.SkillApi;
import org.springy.som.modulith.domain.skill.api.SkillDeletedEvent;

import java.util.List;

import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class SkillService implements SkillApi {
    private final SkillRepository SkillRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SkillService(SkillRepository SkillRepository, ApplicationEventPublisher eventPublisher) {
        this.SkillRepository = SkillRepository;
        this.eventPublisher = eventPublisher;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllSkillsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<SkillDocument> getAllSkills() {
        try {
            return SkillRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAllSkills", ex);
            throw new SkillPersistenceException("Failed to load Skills "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getSkillByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SkillDocument getSkillById(String id) {
        requireText(id, DomainGuards.skillIdMissing());

        try {
            SkillDocument SkillDocument = SkillRepository.findSkillById(id);
            if (SkillDocument == null) {
                throw new SkillNotFoundException(id);
            }
            return SkillDocument;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getSkillById id={}", id, ex);
            throw new SkillPersistenceException("Failed to load Skill: " + id + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getSkillByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SkillDocument getSkillByName(String name) {
        requireText(name, DomainGuards.skillNameMissing());

        try {
            SkillDocument SkillDocument = SkillRepository.findSkillByName(name);
            if (SkillDocument == null) {
                throw new SkillNotFoundException(name);
            }
            return SkillDocument;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getSkillById id={}", name, ex);
            throw new SkillPersistenceException("Failed to load Skill: " + name + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SkillDocument createSkill(SkillDocument skillDocument) {
        requireEntityWithId(skillDocument, SkillDocument::getId, DomainGuards.skillIdMissing(), DomainGuards.skillIdMissing());

        try {
            // if (SkillRepository.existsById(SkillDocument.getSkillId())) throw new SkillConflictException(...)
            return SkillRepository.save(skillDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createSkill SkillId={}", safeId(skillDocument, SkillDocument::getId), ex);
            throw new SkillPersistenceException("Failed to create SkillDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SkillDocument saveSkillForId(String id, SkillDocument skillDocument) {
        SkillDocument existing = SkillRepository.findSkillById(id);
        requireText(existing.getId(), DomainGuards.skillIdMissing());
        requireEntityWithId(existing, SkillDocument::getId, DomainGuards.skillMissing(), DomainGuards.skillIdMissing());

        return SkillRepository.save(skillDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteSkillById(String id) {
        requireText(id, DomainGuards.skillIdMissing());

        try {
            if (!SkillRepository.existsById(id)) {
                throw new SkillNotFoundException(id);
            }
            SkillRepository.deleteById(id);
            eventPublisher.publishEvent(new SkillDeletedEvent(id));
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteSkillById id={}", id, ex);
            throw new SkillPersistenceException("Failed to delete Skill: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllSkills() {
        try {
            List<String> SkillIds = SkillRepository.findAll()
                    .stream()
                    .map(SkillDocument::getId)
                    .toList();
            long itemCount = SkillIds.size();
            SkillRepository.deleteAll();
            for (String SkillId : SkillIds) {
                eventPublisher.publishEvent(new SkillDeletedEvent(SkillId));
            }
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllSkills", ex);
            throw new SkillPersistenceException("Failed to delete all Skills "+ ex);
        }
    }

    private List<SkillDocument> getAllSkillsFallback(Throwable t) {
        log.warn("Fallback getAllSkills due to {}", t.toString());
        return List.of();
    }

    private SkillDocument getSkillByIdFallback(String id, Throwable t) {
        log.warn("Fallback getSkillById id={} due to {}", id, t.toString());
        throw new SkillPersistenceException("SkillDocument lookup temporarily unavailable: " + id+" "+t);
    }
}
