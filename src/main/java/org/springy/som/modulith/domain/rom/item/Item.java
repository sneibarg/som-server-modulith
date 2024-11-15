package org.springy.som.modulith.domain.rom.item;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Items")
public class Item {
    private String areaId;
    private String vnum;
    private String name;
    private String shortDescription;
    private String longDescription;
    private String description;
    private String itemType;
    private String weight;
    private String extraFlags;
    private String wearFlags;
    private String value;
    private String level;
    private List<String> affectData;
    private List<String> extraDescr;


    @Id
    private String id;
}
