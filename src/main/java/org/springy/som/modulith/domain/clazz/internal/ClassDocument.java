package org.springy.som.modulith.domain.clazz.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("RomClasses")
public class ClassDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String whoName;
    private String primaryAttribute;
    private String startingWeapon;
    private String baseGroup;
    private String defaultGroup;
    private int skillAdept;
    private int thac0_00;
    private int thac0_32;
    private int hpMin;
    private int hpMax;
    private boolean fMana;

    @Id
    private String id;
}
