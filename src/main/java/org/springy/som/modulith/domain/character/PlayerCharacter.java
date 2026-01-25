package org.springy.som.modulith.domain.character;

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
    private String sex;
    private String characterClass;
    private String roomId;
    private String areaId;
    private String guild;
    private String role;
    private Boolean cloaked;
    private int health;
    private int mana;
    private int movement;
    private int level;
    private int experience;
    private int accumulatedExperience;
    private int trains;
    private int practices;
    private int gold;
    private int silver;
    private int wimpy;
    private int position; // stand, rest, sleep
    private int maxWeight;
    private int maxItems;
    private int reputation;
    // defense stats
    private int piercing;
    private int bashing;
    private int slashing;
    private int magic;

    @Id
    private String id;
}
