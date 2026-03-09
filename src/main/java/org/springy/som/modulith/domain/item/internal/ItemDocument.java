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
    private int level;
    private int weight;
    private int cost;
    private String condition;

    private List<String> affectData;
    private List<String> extraDescr;

    @Id
    private String id;
}

/*
Inserting item: {'areaId': '69af25413150411a9074f07b', 'vnum': '8904', 'name': 'kernel kernels', 'shortDescription': 'some small yellow kernels', 'longDescription': 'Some small yellow kernels have been accidentally left here.', 'material': 'oldstyle', 'itemType': 'food', 'extraFlags': '0', 'wearFlags': 'AO', 'value0': '1', 'value1': '1', 'value2': '0', 'value3': '0', 'value4': '0', 'level': 0, 'weight': 10, 'cost': 0, 'condition': 'P', 'affectData': [], 'extraDescr': [{'keyword': 'kernel kernels', 'description': 'Look fairly dull...'}], 'id': '69af25415a6727134a842606'}
2026-03-09 15:53:39,184 [INFO] Area: Inserting item: {'areaId': '69af25404fb1ab6974638d31', 'vnum': '2210', 'name': 'belt silk white', 'shortDescription': 'a white silk belt', 'longDescription': 'A white belt made of the finest silk is hanging here.', 'material': 'oldstyle', 'itemType': 'armor', 'extraFlags': '0', 'wearFlags': 'AL', 'value0': '6', 'value1': '6', 'value2': '6', 'value3': '2', 'value4': '0', 'level': 17, 'weight': 250, 'cost': 3900, 'condition': 'P', 'affectData': ['A'], 'extraDescr': [], 'id': '69af25402f652192226687f7'}

 */