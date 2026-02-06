package org.springy.som.modulith.domain.area.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import org.springy.som.modulith.domain.area.api.AreaApi;
import org.springy.som.modulith.util.DomainGuards;

import java.util.List;

import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class AreaService implements AreaApi {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllAreasFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<AreaDocument> getAllAreas() {
        try {
            return areaRepository.findAll();
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAllAreas", ex);
            throw new AreaPersistenceException("Failed to load areas "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAreaByIdFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public AreaDocument getAreaById(String id) {
        requireText(id, DomainGuards.areaIdMissing());

        try {
            AreaDocument areaDocument = areaRepository.findAreaByAreaId(id);
            if (areaDocument == null) {
                throw new AreaNotFoundException(id);
            }
            return areaDocument;
        } catch (AreaNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAreaById id={}", id, ex);
            throw new AreaPersistenceException("Failed to load area: " + id + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public AreaDocument createArea(AreaDocument areaDocument) {
        requireEntityWithId(areaDocument, AreaDocument::getId, DomainGuards.areaMissing(), DomainGuards.areaIdMissing());

        try {
            // if (areaRepository.existsById(areaDocument.getAreaId())) throw new AreaConflictException(...)
            return areaRepository.save(areaDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createArea areaId={}", safeId(areaDocument, AreaDocument::getId), ex);
            throw new AreaPersistenceException("Failed to create areaDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public AreaDocument saveAreaForId(String id, AreaDocument areaDocument) {
        requireText(id, DomainGuards.areaIdMissing());
        requireEntityWithId(areaDocument, AreaDocument::getId, DomainGuards.areaMissing(), DomainGuards.areaIdMissing());

        return areaRepository.save(getAreaById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteAreaById(String id) {
        requireText(id, DomainGuards.areaIdMissing());

        try {
            if (!areaRepository.existsById(id)) {
                throw new AreaNotFoundException(id);
            }
            areaRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAreaById id={}", id, ex);
            throw new AreaPersistenceException("Failed to delete area: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllAreas() {
        try {
            long itemCount = areaRepository.count();
            areaRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllAreas", ex);
            throw new AreaPersistenceException("Failed to delete all areas "+ ex);
        }
    }

    private List<AreaDocument> getAllAreasFallback(Throwable t) {
        log.warn("Fallback getAllAreas due to {}", t.toString());
        return List.of();
    }

    private AreaDocument getAreaByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAreaById id={} due to {}", id, t.toString());
        throw new AreaPersistenceException("AreaDocument lookup temporarily unavailable: " + id+" "+t);
    }
}
