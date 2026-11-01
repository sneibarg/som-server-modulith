package org.springy.som.modulith.prompt.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Document("AgentPrompts")
public class PromptDocument {
    @Id
    @JsonIgnore
    private String id;
    private String name;
    private String model;
    private String system;
    private String user;
    private Map<String, Object> jsonSchema = new LinkedHashMap<>();
    private Map<String, Map<String, Object>> tokens = new LinkedHashMap<>();
}
