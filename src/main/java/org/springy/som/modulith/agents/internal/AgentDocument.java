package org.springy.som.modulith.agents.internal;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Agents")
public class AgentDocument {
    @Id
    private String id;
    private String name;
    private String accountName;
    private String characterName;
    private Boolean enabled;
    private String description;
}
