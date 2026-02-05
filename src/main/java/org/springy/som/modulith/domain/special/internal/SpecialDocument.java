package org.springy.som.modulith.domain.special.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document("Specials")
public class SpecialDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String mobVnum;
    private String specialFunction;
    private String comment;

    @Id
    private String id;
}
