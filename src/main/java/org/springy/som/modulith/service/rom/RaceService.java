package org.springy.som.modulith.service.rom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.rom.race.RomRace;
import org.springy.som.modulith.repository.rom.RaceRepository;

import java.util.List;

@RestController
public class RaceService {
    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    @GetMapping
    @RequestMapping("/api/v1/races")
    public List<RomRace> getAllAreas() {
        return raceRepository.findAll();
    }

    @GetMapping
    @RequestMapping("/api/v1/race")
    public RomRace findRaceById(@RequestParam String raceId) {
        return raceRepository.findRomRaceById(raceId);
    }
}
