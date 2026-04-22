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
    private String characterFlags;
    private String armorClass;
    private String characterAttributes;
    private String temporalMechanics;
    private String equipped;
    private Boolean cloaked;
    private Map<String, Boolean> promptFormat;
    private List<String> inventory;
    private List<String> effects;
    private List<String> skills;
    private List<String> spells;
    private int hit;
    private int maxHit;
    private int mana;
    private int maxMana;
    private int movement;
    private int maxMovement;
    private int level;
    private int gold;
    private int silver;
    private int trust;
    private int invisLevel;
    private int incogLevel;

    @Id
    private String id;
}
