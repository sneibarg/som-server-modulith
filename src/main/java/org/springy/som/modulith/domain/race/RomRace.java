package org.springy.som.modulith.domain.race;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
    {
    "race name",     short name,     points,    { class multipliers },
    { bonus skills },
    { base stats },        { max stats },        size
    },

    {
     "human", "Human", 0, {100, 100, 100, 100},
     {""},
     {13, 13, 13, 13, 13}, {18, 18, 18, 18, 18}, SIZE_MEDIUM},

    {
     "elf", " Elf ", 5, {100, 125, 100, 120},
     {"sneak", "hide"},
     {12, 14, 13, 15, 11}, {16, 20, 18, 21, 15}, SIZE_SMALL},

    {
     "dwarf", "Dwarf", 8, {150, 100, 125, 100},
     {"berserk"},
     {14, 12, 14, 10, 15}, {20, 16, 19, 14, 21}, SIZE_MEDIUM},

    {
     "giant", "Giant", 6, {200, 150, 150, 105},
     {"bash", "fast healing"},
     {16, 11, 13, 11, 14}, {22, 15, 18, 15, 20}, SIZE_LARGE}
 */

@Data
@Document("RomRaces")
public class RomRace {
    @NotBlank(message = "name must not be blank")
    private String name;
    private String size;
    private List<String> skills;
    private List<Integer> classMultiplier;
    private int points;
    private int str;
    private int maxStr;
    private int INT;
    private int maxInt;
    private int con;
    private int maxCon;
    private int wis;
    private int maxWis;
    private int dex;
    private int maxDex;
}
