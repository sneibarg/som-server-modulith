package org.springy.som.modulith.domain.area.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Areas")
public class AreaDocument {
    private String author;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String suggestedLevelRange;
    private List<String> rooms;
    private List<String> mobiles;
    private List<String> objects;
    private List<String> shops;
    private List<String> resets;
    private List<String> specials;

    @Id
    private String id;
}