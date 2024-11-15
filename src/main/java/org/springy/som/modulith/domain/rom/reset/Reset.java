package org.springy.som.modulith.domain.rom.reset;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Resets")
public class Reset {
    private String areaId;
    private String resetType;
    private String comment;
    private List<String> args;


    @Id
    private String id;
}
