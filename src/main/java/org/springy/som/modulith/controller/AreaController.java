package org.springy.som.modulith.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.AreaService;
import org.springy.som.modulith.domain.area.Area;
import java.util.List;

@RestController
@RequestMapping("/api/v1/areas")
public class AreaController {
    private final AreaService areaService;

    public AreaController(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public List<Area> getAllAreas() {
        return areaService.getAllAreas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Area> getAreaById(@PathVariable("id") String id) {
        return ResponseEntity.ok(areaService.getAreaById(id));
    }

    @PostMapping
    public ResponseEntity<Area> createArea(@RequestBody Area area) {
        return new ResponseEntity<>(areaService.createArea(area), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Area> updateArea(@PathVariable("id") String id, @RequestBody Area area) {
        Area areaData = areaService.getAreaById(id);
        if (areaData == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(areaService.saveArea(area), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteArea(@PathVariable("id") String id) {
        try {
            areaService.deleteAreaById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(areaService.deleteAllAreas())
                .append(" Area objects.");
        return response.toString();
    }
}
