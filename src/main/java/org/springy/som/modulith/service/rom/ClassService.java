package org.springy.som.modulith.service.rom;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.rom.clazz.RomClass;
import org.springy.som.modulith.repository.rom.ClassRepository;

import java.util.List;

@RestController
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @GetMapping
    @RequestMapping("/api/v1/classes")
    public List<RomClass> getAllClasses() {
        return classRepository.findAll();
    }

    @GetMapping
    @RequestMapping("/api/v1/class")
    public RomClass getClassById(@RequestParam String classId) {
        return classRepository.findRomClassById(classId);
    }
}
