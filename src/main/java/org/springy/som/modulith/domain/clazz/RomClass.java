package org.springy.som.modulith.domain.clazz;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/*
    {
     "mage", "Mag", STAT_INT, OBJ_VNUM_SCHOOL_DAGGER,
     {3018, 9618}, 75, 20, 6, 6, 8, TRUE,
     "mage basics", "mage default"},

    {
     "cleric", "Cle", STAT_WIS, OBJ_VNUM_SCHOOL_MACE,
     {3003, 9619}, 75, 20, 2, 7, 10, TRUE,
     "cleric basics", "cleric default"},

    {
     "thief", "Thi", STAT_DEX, OBJ_VNUM_SCHOOL_DAGGER,
     {3028, 9639}, 75, 20, -4, 8, 13, FALSE,
     "thief basics", "thief default"},

    {
     "warrior", "War", STAT_STR, OBJ_VNUM_SCHOOL_SWORD,
     {3022, 9633}, 75, 20, -10, 11, 15, FALSE,
     "warrior basics", "warrior default"}
 */

@Data
@Document("RomClasses")
public class RomClass {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String whoName;
    private String primaryAttribute;
    private String startingWeapon;
    private String baseGroup;
    private String defaultGroup;
    private int skillAdept;
    private int thac0_00;
    private int thac0_32;
    private int hpMin;
    private int hpMax;
    private boolean fMana;

    @Id
    private String id;
}
