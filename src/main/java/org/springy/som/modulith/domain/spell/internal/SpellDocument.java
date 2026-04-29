package org.springy.som.modulith.domain.spell.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@Document("Spells")
public class SpellDocument {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String kind;
    private String handlerId;
    private String target;
    private String minPosition;
    private String nounDamage;
    private String msgOff;
    private String msgObj;
    private String functionName;
    private Map<String, Integer> levelByClass;
    private Map<String, Integer> ratingByClass;
    private int slot;
    private int minMana;
    private int beats;
    private List<Map<String, Object>> affectData;
    private List<String> lambdas;

    @Id
    private String id;
}