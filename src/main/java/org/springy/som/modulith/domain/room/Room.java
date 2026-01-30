package org.springy.som.modulith.domain.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Rooms")
public class Room {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String vnum;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String description;
    private String exitEast;
    private String exitSouth;
    private String exitNorth;
    private String exitWest;
    private String exitUp;
    private String exitDown;
    private boolean pvp;
    private boolean spawn;
    private int spawnTimer;
    private int spawnTime;
    private int teleDelay;
    private int roomFlags;
    private int sectorType;
    private List<String> mobiles;
    private List<String> alternateRoutes;
    private List<String> extraDescription;

    @Id
    private String id;
}
