package org.springy.som.modulith.domain.command;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Commands")
public class Command {
    private String name;
    private String message;
    private String role;
    private String usage;
    private String skillId;
    private String shortcuts;
    private List<String> lambda;
    private List<String> function;
    private boolean enabled;

    @Id
    private String id;
}
