package org.springy.som.modulith.domain.command.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Helps")
public class HelpDocument {
    @NotBlank(message = "keyword must not be blank")
    private String keyword;
    private int level;
    private String text;

    @Id
    private String id;
}
