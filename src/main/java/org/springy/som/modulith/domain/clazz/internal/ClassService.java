package org.springy.som.modulith.domain.clazz.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.area.internal.AreaDocument;
import org.springy.som.modulith.exception.clazz.RomClassNotFoundException;
import org.springy.som.modulith.exception.clazz.RomClassPersistenceException;
import org.springy.som.modulith.util.ServiceGuards;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.romClassIdMissing;
import static org.springy.som.modulith.util.DomainGuards.romClassMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;

@Service
@Slf4j
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRomClassesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<RomClassDocument> getAllClasses() {
        return classRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getRomClassByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClassDocument getRomClassById(@RequestParam String classId) {
        return classRepository.findRomClassById(classId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClassDocument createRomClass(@Valid @RequestBody RomClassDocument romClassDocument) {
        requireEntityWithId(romClassDocument, RomClassDocument::getId, romClassMissing(), romClassIdMissing());

        try {
            // if (areaRepository.existsById(area.getAreaId())) throw new AreaConflictException(...)
            return classRepository.save(romClassDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createRomClass romClassId={}", ServiceGuards.safeId(romClassDocument, RomClassDocument::getId), ex);
            throw new RomClassPersistenceException("Failed to create area"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClassDocument saveRomClassForId(String id, RomClassDocument romClassDocument) {
        requireText(id, romClassIdMissing());
        requireEntityWithId(romClassDocument, RomClassDocument::getId, romClassMissing(), romClassIdMissing());

        return classRepository.save(getRomClassById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteRomClassById(String id) {
        requireText(id, romClassIdMissing());

        try {
            if (!classRepository.existsById(id)) {
                throw new RomClassNotFoundException(id);
            }
            classRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAreaById id={}", id, ex);
            throw new RomClassPersistenceException("Failed to delete area: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllRomClasses() {
        try {
            long itemCount = classRepository.count();
            classRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllAreas", ex);
            throw new RomClassPersistenceException("Failed to delete all areas "+ ex);
        }
    }

    private List<RomClassDocument> getAllRomClassesFallback(Throwable t) {
        log.warn("Fallback getAllRomClasses due to {}", t.toString());
        return List.of();
    }

    private AreaDocument getRomClassByIdFallback(String id, Throwable t) {
        log.warn("Fallback getRomClassById id={} due to {}", id, t.toString());
        throw new RomClassPersistenceException("romClass lookup temporarily unavailable: " + id+" "+t);
    }
}
