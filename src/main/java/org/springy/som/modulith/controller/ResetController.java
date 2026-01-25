package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.service.ResetService;

import java.util.List;

@RestController
public class ResetController {
    private final ResetService resetService;

    public ResetController(ResetService resetService) {
        this.resetService = resetService;
    }

    @PostMapping("/api/v1/reset")
    @ResponseBody
    public Reset createReset(@RequestBody Reset reset) {
        return resetService.saveReset(reset);
    }


    @GetMapping(path = "/api/v1/resets")
    @ResponseBody
    public List<Reset> getResets() {
        return resetService.getAllResets();
    }

    @GetMapping(path = "/api/v1/reset")
    @ResponseBody
    public Reset getReset(@RequestParam String resetId) {
        return resetService.getResetById(resetId);
    }

    @DeleteMapping(path = "/api/v1/resets")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(resetService.deleteAllResets())
                .append(" Reset objects.");
        return response.toString();
    }
}
