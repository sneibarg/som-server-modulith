package org.springy.som.modulith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.special.Special;
import org.springy.som.modulith.repository.SpecialRepository;

import java.util.List;

@Slf4j
@Service
public class SpecialService {
    private final SpecialRepository specialRepository;

    public SpecialService(SpecialRepository specialRepository) {
        this.specialRepository = specialRepository;
    }

    public List<Special> getAllSpecials() {
        return specialRepository.findAll();
    }

    public Special getSpecialById(String id) {
        return specialRepository.findById(id).orElse(null);
    }

    public Special saveSpecial(Special special) {
        return specialRepository.save(special);
    }

    public long deleteAllSpecials() {
        long specialCount = specialRepository.count();
        specialRepository.deleteAll();
        return specialCount;
    }
}
