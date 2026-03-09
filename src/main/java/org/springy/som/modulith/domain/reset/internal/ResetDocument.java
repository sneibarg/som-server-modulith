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
    private String command;
    private String arg1;
    private String arg2;
    private String arg3;
    private String arg4;
    private String comment;


    @Id
    private String id;
}
