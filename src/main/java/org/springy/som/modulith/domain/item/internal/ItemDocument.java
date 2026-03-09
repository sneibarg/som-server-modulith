package org.springy.som.modulith.domain.item.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Items")
public class ItemDocument {
    @NotBlank(message = "area ID must not be blank")
    private String areaId;
    private String vnum;
    @NotBlank(message = "name must not be blank")
    private String name;
    private String shortDescription;
    private String longDescription;
    private String material;
    private String itemType;
    private String extraFlags;
    private String wearFlags;
    private String value0;
    private String value1;
    private String value2;
    private String value3;
    private String value4;
    private String level;
    private String weight;
    private String cost;
    private String condition;
    private List<String> affectData;
    private List<String> extraDescr;

    @Id
    private String id;
}
