package org.springy.som.modulith.service.rom;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.reset.Reset;
import org.springy.som.modulith.repository.rom.ResetRepository;

import java.util.List;

@Slf4j
@RestController
public class ResetService {
    private final ResetRepository resetRepository;

    public ResetService(ResetRepository resetRepository) {
        this.resetRepository = resetRepository;
    }

    @PostMapping("/api/v1/reset")
    @ResponseBody
    public Reset createShop(@RequestBody Reset reset) {
        return resetRepository.save(reset);
    }


    @GetMapping(path = "/api/v1/resets")
    @ResponseBody
    public List<Reset> getAllShops() {
        return resetRepository.findAll();
    }

    @GetMapping(path = "/api/v1/reset")
    @ResponseBody
    public Reset getShop(@RequestParam String shopId) {
        return resetRepository.findResetById(shopId);
    }

    @DeleteMapping(path = "/api/v1/resets")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long shopCount = resetRepository.count();
        response.append("Deleted a total of ").append(shopCount).append(" Reset objects.");
        resetRepository.deleteAll();
        return response.toString();
    }
}
