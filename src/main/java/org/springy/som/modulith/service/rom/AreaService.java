package org.springy.som.modulith.service.rom;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.area.Area;
import org.springy.som.modulith.repository.rom.AreaRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaService {

    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    @GetMapping
    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Area> getAreaById(@PathVariable("id") String id) {
        Optional<Area> area = areaRepository.findById(id);
        return area.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        Area savedArea = areaRepository.save(area);
        return new ResponseEntity<>(savedArea, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable("id") String id, @RequestBody Area area) {
        Optional<Area> areaData = areaRepository.findById(id);

        if (areaData.isPresent()) {
            return new ResponseEntity<>(areaRepository.save(area), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArea(@PathVariable("id") String id) {
        try {
            areaRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = areaRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" Area objects.");
        areaRepository.deleteAll();
        return response.toString();
    }
}
