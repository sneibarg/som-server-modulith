package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.special.Special;
import org.springy.som.modulith.repository.rom.SpecialRepository;

import java.util.List;

@Slf4j
@RestController
public class SpecialService {
    private final SpecialRepository specialRepository;

    public SpecialService(SpecialRepository specialRepository) {
        this.specialRepository = specialRepository;
    }

    @PostMapping("/api/v1/special")
    @ResponseBody
    public Special createShop(@RequestBody Special special) {
        return specialRepository.save(special);
    }


    @GetMapping(path = "/api/v1/specials")
    @ResponseBody
    public List<Special> getAllSpecials() {
        return specialRepository.findAll();
    }

    @GetMapping(path = "/api/v1/special")
    @ResponseBody
    public Special getShop(@RequestParam String specialId) {
        return specialRepository.findSpecialById(specialId);
    }

    @DeleteMapping(path = "/api/v1/specials")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long shopCount = specialRepository.count();
        response.append("Deleted a total of ").append(shopCount).append(" Special objects.");
        specialRepository.deleteAll();
        return response.toString();
    }
}
