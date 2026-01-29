package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.exception.area.AreaNotFoundException;
import org.springy.som.modulith.exception.area.AreaPersistenceException;
import org.springy.som.modulith.exception.area.InvalidAreaException;
import org.springy.som.modulith.repository.AreaRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllAreasFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Area> getAllAreas() {
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
    public Area getAreaById(String id) {
        requireId(id);

        try {
            Area area = areaRepository.findAreaByAreaId(id);
            if (area == null) {
                throw new AreaNotFoundException(id);
            }
            return area;
        } catch (AreaNotFoundException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.warn("DB failure in getAreaById id={}", id, ex);
            throw new AreaPersistenceException("Failed to load area: " + id + " "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Area createArea(Area area) {
        requireArea(area);

        try {
            // if (areaRepository.existsById(area.getAreaId())) throw new AreaConflictException(...)
            return areaRepository.save(area);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createArea areaId={}", safeId(area), ex);
            throw new AreaPersistenceException("Failed to create area"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Area saveArea(Area area) {
        requireArea(area);

        return areaRepository.save(area);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Area saveAreaForId(String id, Area area) {
        requireId(id);
        requireArea(area);

        return areaRepository.save(getAreaById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteAreaById(String id) {
        requireId(id);

        try {
            if (!areaRepository.existsById(id)) {
                throw new AreaNotFoundException(id);
            }
            areaRepository.deleteById(id);
        } catch (AreaNotFoundException ex) {
            throw ex;
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

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidAreaException("Area id must be provided");
        }
    }

    private static void requireArea(Area area) {
        if (area == null) {
            throw new InvalidAreaException("Area must be provided");
        }

        requireId(area.getId());
    }

    private static String safeId(Area area) {
        try {
            return area.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<Area> getAllAreasFallback(Throwable t) {
        log.warn("Fallback getAllAreas due to {}", t.toString());
        return List.of();
    }

    private Area getAreaByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAreaById id={} due to {}", id, t.toString());
        throw new AreaPersistenceException("Area lookup temporarily unavailable: " + id+" "+t);
    }
}
