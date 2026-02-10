package org.springy.som.modulith.domain.race.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.race.api.RaceApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.romRaceIdMissing;
import static org.springy.som.modulith.domain.DomainGuards.romRaceMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Service
@Slf4j
public class RaceService implements RaceApi {
    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllRomRacesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<RaceDocument> getAllRaces() {
        return raceRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RaceDocument getRaceByName(@RequestParam String name) {
        return raceRepository.findRomRaceById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RaceDocument getRaceById(@RequestParam String id) {
        return raceRepository.findRomRaceById(id);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RaceDocument createRace(@Valid @RequestBody RaceDocument raceDocument) {
        requireEntityWithId(raceDocument, RaceDocument::getId, romRaceMissing(), romRaceIdMissing());

        try {
            // if (raceRepository.existsById(raceDocument.getRaceId())) throw new RaceConflictException(...)
            return raceRepository.save(raceDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createRomRace romRaceId={}", safeId(raceDocument, RaceDocument::getId), ex);
            throw new RacePersistenceException("Failed to create ROM race"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public RaceDocument saveRaceForId(String id, RaceDocument raceDocument) {
        requireText(id, romRaceIdMissing());
        requireEntityWithId(raceDocument, RaceDocument::getId, romRaceMissing(), romRaceIdMissing());

        return raceRepository.save(getRaceById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteRaceById(String id) {
        requireText(id, romRaceIdMissing());

        try {
            if (!raceRepository.existsById(id)) {
                throw new RaceNotFoundException(id);
            }
            raceRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteRomRaceById id={}", id, ex);
            throw new RacePersistenceException("Failed to delete ROM race: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllRaces() {
        try {
            long itemCount = raceRepository.count();
            raceRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllMobiles", ex);
            throw new RacePersistenceException("Failed to delete all ROM race "+ ex);
        }
    }

    private List<RaceDocument> getAllRomRacesFallback(Throwable t) {
        log.warn("Fallback getAllRomRaces due to {}", t.toString());
        return List.of();
    }

    private RaceDocument getRomRaceByIdFallback(String id, Throwable t) {
        log.warn("Fallback getRomRaceById id={} due to {}", id, t.toString());
        throw new RacePersistenceException("ROM race lookup temporarily unavailable: " + id+" "+t);
    }
}
