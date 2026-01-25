package org.springy.som.modulith.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.lang.reflect.Method;

@RestController
public class AdminController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationContext applicationContext;


    public AdminController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @DeleteMapping("/admin/deleteAll")
    public String deleteAll(@RequestBody String exclusions) throws IOException {
        var repositories = applicationContext.getBeansOfType(MongoRepository.class);
        JsonNode exclusionList = objectMapper.readTree(exclusions.getBytes()).get("exclusions");

        for (var entry : repositories.entrySet()) {
            StringBuilder repositoryName = new StringBuilder();
            repositoryName.append(entry.getKey());
            repositoryName.setCharAt(0, Character.toUpperCase(repositoryName.charAt(0)));
            if (exclusionList.toString().contains(repositoryName.toString()))
                continue;

            try {
                var repository = entry.getValue();
                Method deleteAllMethod = repository.getClass().getMethod("deleteAll");
                deleteAllMethod.invoke(repository);
            } catch (Exception e) {
                return "Error occurred while deleting records from repository: " + repositoryName;
            }
        }
        return "All records deleted successfully (excluding specified repositories).";
    }
}
