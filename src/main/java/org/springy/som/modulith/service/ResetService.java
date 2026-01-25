package org.springy.som.modulith.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.repository.ResetRepository;

import java.util.List;

@Slf4j
@Service
public class ResetService {
    private final ResetRepository resetRepository;

    public ResetService(ResetRepository resetRepository) {
        this.resetRepository = resetRepository;
    }

    public List<Reset> getAllResets() {
        return resetRepository.findAll();
    }

    public Reset getResetById(String id) {
        return resetRepository.findResetById(id);
    }

    public Reset saveReset(Reset reset) {
        return resetRepository.save(reset);
    }

    public long deleteAllResets() {
        long resetCount = resetRepository.count();
        resetRepository.deleteAll();
        return resetCount;
    }
}
