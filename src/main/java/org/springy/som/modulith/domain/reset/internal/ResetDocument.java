package org.springy.som.modulith.domain.reset.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Resets")
public class ResetDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String resetType;
    private String comment;
    private List<String> args;


    @Id
    private String id;
}
