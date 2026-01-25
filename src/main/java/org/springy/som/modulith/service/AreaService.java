package org.springy.som.modulith.service;


import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.repository.AreaRepository;
import java.util.List;

@Service
public class AreaService {
    private final AreaRepository areaRepository;

    public AreaService(AreaRepository areaRepository) {
        this.areaRepository = areaRepository;
    }

    public List<Area> getAllAreas() {
        return areaRepository.findAll();
    }

    public Area getAreaById(String id) {
        return areaRepository.findById(id).orElse(null);
    }

    public Area createArea(Area area) {
        return areaRepository.save(area);
    }

    public Area saveArea(Area area) {
        return areaRepository.save(area);
    }

    public void deleteAreaById(String id) {
        areaRepository.deleteById(id);
    }

    public long deleteAllAreas() {
        long itemCount = areaRepository.count();
        areaRepository.deleteAll();
        return itemCount;
    }
}
