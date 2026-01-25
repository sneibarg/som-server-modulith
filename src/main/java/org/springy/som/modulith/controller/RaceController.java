package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.RaceService;
import org.springy.som.modulith.domain.race.RomRace;

import java.util.List;

@RestController
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @GetMapping
    @RequestMapping("/api/v1/races")
    public List<RomRace> getAllAreas() {
        return raceService.getAllRaces();
    }

    @GetMapping
    @RequestMapping("/api/v1/race")
    public RomRace findRaceById(@RequestParam String raceId) {
        return raceService.getRomRaceById(raceId);
    }
}
