package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.ClassService;
import org.springy.som.modulith.domain.clazz.RomClass;

import java.util.List;

@RestController
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    @RequestMapping("/api/v1/classes")
    public List<RomClass> getAllClasses() {
        return classService.getClasses();
    }

    @GetMapping
    @RequestMapping("/api/v1/class")
    public RomClass getClassById(@RequestParam String classId) {
        return classService.getClassById(classId);
    }
}
