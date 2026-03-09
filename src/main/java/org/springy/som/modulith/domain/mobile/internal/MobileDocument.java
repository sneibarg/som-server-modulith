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
    private String offFlags;
    private String immFlags;
    private String resFlags;
    private String vulnFlags;
    private String startPos;
    private String defaultPos;
    private String sex;
    private String form;
    private String parts;
    private String size;
    private String material;
    private String flags;
    private int level;
    private int hitroll;
    private int hitDiceNumber;
    private int hitDiceType;
    private int hitDiceBonus;
    private int manaDiceNumber;
    private int manaDiceType;
    private int manaDiceBonus;
    private int damageDiceNumber;
    private int damageDiceType;
    private int damageDiceBonus;
    private int acPierce;
    private int acBash;
    private int acSlash;
    private int acExotic;
    private int gold;


    @Id
    private String id;
}
