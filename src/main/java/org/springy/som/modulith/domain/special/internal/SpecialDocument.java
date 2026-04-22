package org.springy.som.modulith.domain.special.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Document("Specials")
public class SpecialDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String mobVnum;
    private String name;
    private String comment;
    private List<String> specialFunction;

    @Id
    private String id;
}
