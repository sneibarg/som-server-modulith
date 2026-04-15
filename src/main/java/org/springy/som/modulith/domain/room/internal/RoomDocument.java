package org.springy.som.modulith.domain.room.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document("Rooms")
public class RoomDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String vnum;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String description;
    private String extraDescription;
    private boolean pvp;
    private boolean spawn;
    private int spawnTimer;
    private int spawnTime;
    private int teleDelay;
    private int roomFlags;
    private int sectorType;
    private List<String> exits;
    private Map<String, String> mobiles;

    @Id
    private String id;
}
