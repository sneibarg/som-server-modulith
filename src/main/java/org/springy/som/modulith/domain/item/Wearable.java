package org.springy.som.modulith.domain.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Wearable")
public class Wearable {
    @NotBlank(message = "character ID must not be blank")
    private String characterId;
    private String light;
    private String neck1;
    private String neck2;
    private String torso;
    private String head;
    private String legs;
    private String feet;
    private String shield;
    private String body;
    private String waist;
    private String wrist;
    private String wielded1;
    private String wielded2;
}
