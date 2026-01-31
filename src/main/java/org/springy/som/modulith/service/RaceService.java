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
import org.springy.som.modulith.domain.race.RomRace;
import org.springy.som.modulith.exception.mobile.MobilePersistenceException;
import org.springy.som.modulith.exception.race.InvalidRomRaceException;
import org.springy.som.modulith.exception.race.RomRaceNotFoundException;
import org.springy.som.modulith.exception.race.RomRacePersistenceException;
import org.springy.som.modulith.repository.RaceRepository;

import java.util.List;

@Service
@Slf4j
public class RaceService {
    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRomRacesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<RomRace> getAllRomRaces() {
        return raceRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomRace getRomRaceByName(@RequestParam String mobileName) {
        return raceRepository.findRomRaceById(mobileName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomRace getRomRaceById(@RequestParam String itemId) {
        return raceRepository.findRomRaceById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomRace createRomRace(@Valid @RequestBody RomRace romRace) {
        requireRomRace(romRace);

        try {
            // if (raceRepository.existsById(romRace.getRomRaceId())) throw new RomRaceConflictException(...)
            return raceRepository.save(romRace);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createRomRace areaId={}", safeId(romRace), ex);
            throw new RomRacePersistenceException("Failed to create ROM race"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RomRace saveRomRaceForId(String id, RomRace romRace) {
        requireId(id);
        requireRomRace(romRace);

        return raceRepository.save(getRomRaceById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteRomRaceById(String id) {
        requireId(id);

        try {
            if (!raceRepository.existsById(id)) {
                throw new RomRaceNotFoundException(id);
            }
            raceRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteRomRaceById id={}", id, ex);
            throw new MobilePersistenceException("Failed to delete ROM race: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllRomRace() {
        try {
            long itemCount = raceRepository.count();
            raceRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllMobiles", ex);
            throw new RomRacePersistenceException("Failed to delete all ROM race "+ ex);
        }
    }

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidRomRaceException("ROM race id must be provided");
        }
    }

    private static void requireRomRace(RomRace romRace) {
        if (romRace == null) {
            throw new InvalidRomRaceException("ROM race must be provided");
        }

        requireId(romRace.getId());
    }

    private static String safeId(RomRace romRace) {
        try {
            return romRace.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<RomRace> getAllRomRacesFallback(Throwable t) {
        log.warn("Fallback getAllRomRaces due to {}", t.toString());
        return List.of();
    }

    private RomRace getRomRacesByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllRomRacesById id={} due to {}", id, t.toString());
        throw new MobilePersistenceException("ROM race lookup temporarily unavailable: " + id+" "+t);
    }
}
