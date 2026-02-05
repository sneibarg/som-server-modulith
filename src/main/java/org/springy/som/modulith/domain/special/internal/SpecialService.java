package org.springy.som.modulith.domain.special.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.exception.special.SpecialNotFoundException;
import org.springy.som.modulith.exception.special.SpecialPersistenceException;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.specialIdMissing;
import static org.springy.som.modulith.util.DomainGuards.specialMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class SpecialService {
    private final SpecialRepository specialRepository;

    public SpecialService(SpecialRepository specialRepository) {
        this.specialRepository = specialRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllSpecialsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<SpecialDocument> getAllSpecials() {
        return specialRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpecialDocument getSpecialByName(@RequestParam String name) {
        return specialRepository.findSpecialById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpecialDocument getSpecialById(@RequestParam String itemId) {
        return specialRepository.findSpecialById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpecialDocument createSpecial(@Valid @RequestBody SpecialDocument specialDocument) {
        requireEntityWithId(specialDocument, SpecialDocument::getId, specialMissing(), specialIdMissing());

        try {
            // if (specialRepository.existsById(specialDocument.getId())) throw new SpecialConflictException(...)
            return specialRepository.save(specialDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createSpecial specialId={}", safeId(specialDocument, SpecialDocument::getId), ex);
            throw new SpecialPersistenceException("Failed to create ROM specialDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SpecialDocument saveSpecialForId(String id, SpecialDocument specialDocument) {
        requireText(id, specialIdMissing());
        requireEntityWithId(specialDocument, SpecialDocument::getId, specialMissing(), specialIdMissing());

        return specialRepository.save(getSpecialById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteSpecialById(String id) {
        requireText(id, specialIdMissing());

        try {
            if (!specialRepository.existsById(id)) {
                throw new SpecialNotFoundException(id);
            }
            specialRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteSpecialById id={}", id, ex);
            throw new SpecialPersistenceException("Failed to delete ROM specials: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllSpecials() {
        try {
            long itemCount = specialRepository.count();
            specialRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllSpecials", ex);
            throw new SpecialPersistenceException("Failed to delete all ROM specials "+ ex);
        }
    }

    private List<SpecialDocument> getAllSpecialsFallback(Throwable t) {
        log.warn("Fallback getAllSpecials due to {}", t.toString());
        return List.of();
    }

    private SpecialDocument getSpecialByIdFallback(String id, Throwable t) {
        log.warn("Fallback getSpecialById id={} due to {}", id, t.toString());
        throw new SpecialPersistenceException("ROM special lookup temporarily unavailable: " + id+" "+t);
    }
}
