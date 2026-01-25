package org.springy.som.modulith.service;

import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.race.RomRace;
import org.springy.som.modulith.repository.RaceRepository;

import java.util.List;

@Service
public class RaceService {
    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public List<RomRace> getAllRaces() {
        return raceRepository.findAll();
    }

    public RomRace getRomRaceById(String id) {
        return raceRepository.findById(id).orElse(null);
    }

}
