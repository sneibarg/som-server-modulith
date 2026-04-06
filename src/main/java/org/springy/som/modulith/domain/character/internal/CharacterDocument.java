package org.springy.som.modulith.domain.character.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document("Characters")
public class CharacterDocument {
    private String accountId;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String title;
    private String description;
    private String race;
    private String sex;
    private String characterClass;
    private String roomId;
    private String areaId;
    private String guild;
    private String role;
    private String act;
    private String comm;
    private String affectedBy;
    private String armorClass;
    private String characterAttributes;
    private String temporalMechanics;
    private Boolean cloaked;
    private List<String> inventory;
    private Map<String, Boolean> promptFormat;
    private int health;
    private int mana;
    private int movement;
    private int level;
    private int experience;
    private int accumulatedExperience;
    private int gold;
    private int silver;
    private int trust;

    @Id
    private String id;
}
