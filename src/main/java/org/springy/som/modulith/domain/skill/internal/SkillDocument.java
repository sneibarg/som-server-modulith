package org.springy.som.modulith.domain.skill.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Document("Skills")
public class SkillDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String kind;
    private String handlerId;
    private String target;
    private String minPosition;
    private String nounDamage;
    private String msgOff;
    private String msgObj;
    private Map<String, Integer> levelByClass;
    private Map<String, Integer> ratingByClass;
    private int slot;
    private int minMana;
    private int beats;

    @Id
    private String id;
}