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
import org.springy.som.modulith.domain.special.Special;
import org.springy.som.modulith.exception.special.SpecialNotFoundException;
import org.springy.som.modulith.exception.special.SpecialPersistenceException;
import org.springy.som.modulith.repository.SpecialRepository;

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
    public List<Special> getAllSpecials() {
        return specialRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Special getSpecialByName(@RequestParam String name) {
        return specialRepository.findSpecialById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Special getSpecialById(@RequestParam String itemId) {
        return specialRepository.findSpecialById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Special createSpecial(@Valid @RequestBody Special special) {
        requireEntityWithId(special, Special::getId, specialMissing(), specialIdMissing());

        try {
            // if (specialRepository.existsById(special.getSpecialById())) throw new SpecialConflictException(...)
            return specialRepository.save(special);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createSpecial areaId={}", safeId(special, Special::getId), ex);
            throw new SpecialPersistenceException("Failed to create ROM special"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Special saveSpecialForId(String id, Special special) {
        requireText(id, specialIdMissing());
        requireEntityWithId(special, Special::getId, specialMissing(), specialIdMissing());

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

    private List<Special> getAllSpecialFallback(Throwable t) {
        log.warn("Fallback getAllSpecials due to {}", t.toString());
        return List.of();
    }

    private Special getSpecialsByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllSpecialById id={} due to {}", id, t.toString());
        throw new SpecialPersistenceException("ROM special lookup temporarily unavailable: " + id+" "+t);
    }
}
