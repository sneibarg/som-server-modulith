package org.springy.som.modulith.domain.rom.character;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
    private String role;
    private String guild;
    private int level;
    private int currentHealth;
    private int maxHealth;
    private boolean cloaked;
    private List<String> inventory;
    private List<String> statuses;
    private List<String> skills;

    @Id
    private String id;
}
