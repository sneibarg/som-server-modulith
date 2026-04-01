package org.springy.som.modulith.domain.command.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.api.HelpApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.helpIdMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class HelpService implements HelpApi {
    private final HelpRepository helpRepository;

    public HelpService(HelpRepository helpRepository) {
        this.helpRepository = helpRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllHelpsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<HelpDocument> getAllHelps() {
        return helpRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public HelpDocument getHelpByKeyword(@RequestParam String keyword) {
        return helpRepository.findHelpByKeyword(keyword);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public HelpDocument getHelpById(@RequestParam String helpId) {
        return helpRepository.findHelpById(helpId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public HelpDocument createHelp(@Valid @RequestBody HelpDocument helpDocument) {
        requireEntityWithId(helpDocument, HelpDocument::getId, helpIdMissing(), helpIdMissing());

        try {
            // if (helpRepository.existsById(helpDocument.getId())) throw new HelpConflictException(...)
            return helpRepository.save(helpDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createHelp helpId={}", safeId(helpDocument, HelpDocument::getId), ex);
            throw new HelpPersistenceException("Failed to create helpDocument" + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public HelpDocument saveHelpForId(String id, HelpDocument helpDocument) {
        HelpDocument existing = helpRepository.findHelpById(id);
        requireText(existing.getId(), helpIdMissing());
        requireEntityWithId(existing, HelpDocument::getId, helpIdMissing(), helpIdMissing());

        return helpRepository.save(helpDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteHelpById(String id) {
        requireText(id, helpIdMissing());

        try {
            if (!helpRepository.existsById(id)) {
                throw new HelpNotFoundException(id);
            }
            helpRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteHelpById id={}", id, ex);
            throw new HelpPersistenceException("Failed to delete help: " + id + " " + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllHelps() {
        try {
            long itemCount = helpRepository.count();
            helpRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllHelps", ex);
            throw new HelpPersistenceException("Failed to delete all helps " + ex);
        }
    }

    private List<HelpDocument> getAllHelpsFallback(Throwable t) {
        log.warn("Fallback getAllHelps due to {}", t.toString());
        return List.of();
    }

    private HelpDocument getHelpByIdFallback(String id, Throwable t) {
        log.warn("Fallback getHelpById id={} due to {}", id, t.toString());
        throw new HelpPersistenceException("HelpDocument lookup temporarily unavailable: " + id + " " + t);
    }
}
