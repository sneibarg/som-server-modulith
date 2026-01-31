package org.springy.som.modulith.service;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.exception.reset.ResetNotFoundException;
import org.springy.som.modulith.exception.reset.ResetPersistenceException;
import org.springy.som.modulith.repository.ResetRepository;

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
    public List<Reset> getAllResets() {
        return resetRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Reset getResetByName(@RequestParam String name) {
        return resetRepository.findResetById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Reset getResetById(@RequestParam String itemId) {
        return resetRepository.findResetById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Reset createReset(@Valid @RequestBody Reset reset) {
        requireEntityWithId(reset, Reset::getId, resetMissing(), resetIdMissing());

        try {
            // if (resetRepository.existsById(reset.getResetById())) throw new ResetConflictException(...)
            return resetRepository.save(reset);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createReset resetId={}", safeId(reset, Reset::getId), ex);
            throw new ResetPersistenceException("Failed to create ROM race"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Reset saveResetForId(String id, Reset reset) {
        requireText(id, resetIdMissing());
        requireEntityWithId(reset, Reset::getId, resetMissing(), resetIdMissing());

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
            log.warn("DB failure in deleteAllMobiles", ex);
            throw new ResetPersistenceException("Failed to delete all ROM race "+ ex);
        }
    }

    private List<Reset> getAllResetsFallback(Throwable t) {
        log.warn("Fallback getAllResets due to {}", t.toString());
        return List.of();
    }

    private Reset getResetsByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllResetsById id={} due to {}", id, t.toString());
        throw new ResetPersistenceException("ROM reset lookup temporarily unavailable: " + id+" "+t);
    }
}
