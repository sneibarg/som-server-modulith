package org.springy.som.modulith.domain.mobile.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Mobiles")
public class MobileDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String vnum;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String shortDescription;
    private String longDescription;
    private String description;
    private String race;
    private String actFlags;
    private String affectFlags;
    private String alignment;
    private String group;
    private String damType;
    private String combatFlags;
    private String hitDice;
    private String manaDice;
    private String damageDice;
    private String armorClass;
    private String startPos;
    private String defaultPos;
    private String sex;
    private String size;
    private String material;
    private String flags;
    private int form;
    private int parts;
    private int level;
    private int hitroll;
    private int gold;
    private int silver;
    private int pulseWait;
    private int pulseDaze;


    @Id
    private String id;
}
