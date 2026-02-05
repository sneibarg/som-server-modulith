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
    private String actFlags;
    private String affectFlags;
    private String alignment;
    private String race;
    private String sex;
    private String startPos;
    private String defaultPos;
    private String hitroll;
    private String damage;

    private int level;
    private int gold;

    @Id
    private String id;
}
