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
    private String roomId;
    private String areaId;
    private String guild;
    private String role;
    private Map<String, Object> equipped;
    private Map<String, Boolean> promptFormat;
    private Map<String, Object> characterClass;
    private Map<String, Map<String, String>> characterFlags;
    private Map<String, Integer> armorClass;
    private Map<String, Object> characterAttributes;
    private Map<String, Object> temporalMechanics;
    private List<Map<String, Object>> inventory;
    private List<Map<String, Object>> effects;
    private List<Map<String, Object>> skills;
    private List<Map<String, Object>> spells;
    private Boolean cloaked;
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
