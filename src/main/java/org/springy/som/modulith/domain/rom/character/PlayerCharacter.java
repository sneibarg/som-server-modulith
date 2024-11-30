package org.springy.som.modulith.domain.rom.character;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Characters")
public class PlayerCharacter {
    private String accountId;
    private String name;
    private String title;
    private String description;
    private String race;
    private String characterClass;
    private String roomId;
    private String areaId;
    private String guild;
    private Boolean cloaked;
    private int health;
    private int level;

    @Id
    private String id;
}
