package org.springy.som.modulith.domain.command.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Commands")
public class CommandDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String message;
    private String role;
    private String usage;
    private String skillId;
    private String shortcuts;
    private String position;
    private String log;
    private String help;
    private String level;
    private List<String> lambdas;
    private List<String> function;
    private boolean enabled;

    @Id
    private String id;
}
