package org.springy.som.modulith.domain.race.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document("RomRaces")
public class RaceDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String size;
    private List<String> skills;
    private List<Integer> classMultiplier;
    private int points;
    private int str;
    private int maxStr;
    private int INT;
    private int maxInt;
    private int con;
    private int maxCon;
    private int wis;
    private int maxWis;
    private int dex;
    private int maxDex;

    @Id
    private String id;
}
