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
    private String level;
    private String hitroll;
    private String damage;
    private String race;
    private String sex;
    private String gold;
    private String startPos;
    private String defaultPos;

    @Id
    private String id;
}
