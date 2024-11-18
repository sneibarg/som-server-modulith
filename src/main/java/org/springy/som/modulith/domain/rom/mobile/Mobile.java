package org.springy.som.modulith.domain.rom.mobile;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Mobiles")
public class Mobile {
    private String areaId;
    private String vnum;
    private String name;
    private String shortDescription;
    private String longDescription;
    private String description;
    private String actFlags;
    private String affectFlags;
    private String alignment;
    private String race;
    private String sex;
    private String startPos;
    private String defaultPos;

    private int level;
    private int hitroll;
    private int damage;
    private int gold;

    @Id
    private String id;
}
