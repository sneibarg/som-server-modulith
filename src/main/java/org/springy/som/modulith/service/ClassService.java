package org.springy.som.modulith.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.clazz.RomClass;
import org.springy.som.modulith.repository.ClassRepository;

import java.util.List;

@Service
public class ClassService {
    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<RomClass> getClasses() {
        return classRepository.findAll();
    }

    public RomClass getClassById(@RequestParam String classId) {
        return classRepository.findRomClassById(classId);
    }
}
