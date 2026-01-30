package org.springy.som.modulith.domain.item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Items")
public class Item {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String vnum;
    @NotBlank(message = "name must not be blank")
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
