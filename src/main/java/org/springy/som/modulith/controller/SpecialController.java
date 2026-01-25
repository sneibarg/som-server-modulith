package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.SpecialService;
import org.springy.som.modulith.domain.special.Special;

import java.util.List;

@RestController
public class SpecialController {
    private final SpecialService specialService;

    public SpecialController(SpecialService specialService) {
        this.specialService = specialService;
    }

    @PostMapping("/api/v1/special")
    @ResponseBody
    public Special createShop(@RequestBody Special special) {
        return specialService.saveSpecial(special);
    }

    @GetMapping(path = "/api/v1/specials")
    @ResponseBody
    public List<Special> getAllSpecials() {
        return specialService.getAllSpecials();
    }

    @GetMapping(path = "/api/v1/special")
    @ResponseBody
    public Special getShop(@RequestParam String specialId) {
        return specialService.getSpecialById(specialId);
    }

    @DeleteMapping(path = "/api/v1/specials")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ").append(specialService.deleteAllSpecials()).append(" Special objects.");
        return response.toString();
    }
}
