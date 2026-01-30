package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.domain.clazz.RomClass;
import org.springy.som.modulith.exception.clazz.RomClassNotFoundException;
import org.springy.som.modulith.exception.clazz.RomClassPersistenceException;
import org.springy.som.modulith.exception.clazz.InvalidRomClassException;
import org.springy.som.modulith.repository.ClassRepository;

import java.util.List;

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
    public List<RomClass> getAllClasses() {
        return classRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getRomClassByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClass getRomClassById(@RequestParam String classId) {
        return classRepository.findRomClassById(classId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClass createRomClass(@Valid @RequestBody RomClass romClass) {
        requireRomClass(romClass);

        try {
            // if (areaRepository.existsById(area.getAreaId())) throw new AreaConflictException(...)
            return classRepository.save(romClass);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createArea areaId={}", safeId(romClass), ex);
            throw new RomClassPersistenceException("Failed to create area"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomClass saveRomClassForId(String id, RomClass romClass) {
        requireId(id);
        requireRomClass(romClass);

        return classRepository.save(getRomClassById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteRomClassById(String id) {
        requireId(id);

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

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidRomClassException("RomClass id must be provided");
        }
    }

    private static void requireRomClass(RomClass romClass) {
        if (romClass == null) {
            throw new InvalidRomClassException("RomClass must be provided");
        }

        requireId(romClass.getId());
    }

    private static String safeId(RomClass romClass) {
        try {
            return romClass.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<RomClass> getAllRomClassesFallback(Throwable t) {
        log.warn("Fallback getAllRomClasses due to {}", t.toString());
        return List.of();
    }

    private Area getRomClassByIdFallback(String id, Throwable t) {
        log.warn("Fallback getRomClassById id={} due to {}", id, t.toString());
        throw new RomClassPersistenceException("romClass lookup temporarily unavailable: " + id+" "+t);
    }
}
