package org.springy.som.modulith.domain.reset.internal;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.resetIdMissing;
import static org.springy.som.modulith.util.DomainGuards.resetMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class ResetService {
    private final ResetRepository resetRepository;

    public ResetService(ResetRepository resetRepository) {
        this.resetRepository = resetRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllResetsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<ResetDocument> getAllResets() {
        return resetRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ResetDocument getResetByName(@RequestParam String name) {
        return resetRepository.findResetById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ResetDocument getResetById(@RequestParam String itemId) {
        return resetRepository.findResetById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ResetDocument createReset(@Valid @RequestBody ResetDocument resetDocument) {
        requireEntityWithId(resetDocument, ResetDocument::getId, resetMissing(), resetIdMissing());

        try {
            // if (resetRepository.existsById(resetDocument.getId())) throw new ResetConflictException(...)
            return resetRepository.save(resetDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createReset resetId={}", safeId(resetDocument, ResetDocument::getId), ex);
            throw new ResetPersistenceException("Failed to create ROM race"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ResetDocument saveResetForId(String id, ResetDocument resetDocument) {
        requireText(id, resetIdMissing());
        requireEntityWithId(resetDocument, ResetDocument::getId, resetMissing(), resetIdMissing());

        return resetRepository.save(getResetById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteResetById(String id) {
        requireText(id, resetIdMissing());

        try {
            if (!resetRepository.existsById(id)) {
                throw new ResetNotFoundException(id);
            }
            resetRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteRomRaceById id={}", id, ex);
            throw new ResetPersistenceException("Failed to delete ROM reset: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllResets() {
        try {
            long itemCount = resetRepository.count();
            resetRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllResets", ex);
            throw new ResetPersistenceException("Failed to delete all ROM resets "+ ex);
        }
    }

    private List<ResetDocument> getAllResetsFallback(Throwable t) {
        log.warn("Fallback getAllResets due to {}", t.toString());
        return List.of();
    }

    private ResetDocument getResetByIdFallback(String id, Throwable t) {
        log.warn("Fallback getResetById id={} due to {}", id, t.toString());
        throw new ResetPersistenceException("ROM reset lookup temporarily unavailable: " + id+" "+t);
    }
}
