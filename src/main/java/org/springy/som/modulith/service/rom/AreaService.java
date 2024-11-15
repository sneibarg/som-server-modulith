package org.springy.som.modulith.service.rom;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.area.RomArea;
import org.springy.som.modulith.repository.RomAreaRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaService {

    private final RomAreaRepository romAreaRepository;

    public AreaService(RomAreaRepository romAreaRepository) {
        this.romAreaRepository = romAreaRepository;
    }

    @GetMapping
    public List<RomArea> getAllAreas() {
        return romAreaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RomArea> getAreaById(@PathVariable("id") String id) {
        Optional<RomArea> area = romAreaRepository.findById(id);
        return area.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RomArea> createArea(@RequestBody RomArea romArea) {
        RomArea savedRomArea = romAreaRepository.save(romArea);
        return new ResponseEntity<>(savedRomArea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RomArea> updateArea(@PathVariable("id") String id, @RequestBody RomArea romArea) {
        Optional<RomArea> areaData = romAreaRepository.findById(id);

        if (areaData.isPresent()) {
            return new ResponseEntity<>(romAreaRepository.save(romArea), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArea(@PathVariable("id") String id) {
        try {
            romAreaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = romAreaRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" RomArea objects.");
        romAreaRepository.deleteAll();
        return response.toString();
    }
}
