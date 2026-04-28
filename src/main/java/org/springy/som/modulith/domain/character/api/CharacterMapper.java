package org.springy.som.modulith.domain.character.api;

import org.springy.som.modulith.domain.character.internal.CharacterDocument;

public final class CharacterMapper {

    private CharacterMapper() {}

    public static CharacterView toView(CharacterDocument doc) {
        return new CharacterView(
                doc.getId(),
                doc.getAccountId(),
                doc.getName(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getSex(),
                doc.getRoomId(),
                doc.getAreaId(),
                doc.getGuild(),
                doc.getRole(),
                doc.getEquipped(),
                doc.getPromptFormat(),
                doc.getCharacterRace(),
                doc.getCharacterClass(),
                doc.getCharacterFlags(),
                doc.getArmorClass(),
                doc.getCharacterAttributes(),
                doc.getTemporalMechanics(),
                doc.getInventory(),
                doc.getEffects(),
                doc.getSkills(),
                doc.getSpells(),
                doc.getCloaked(),
                doc.getHit(),
                doc.getMaxHit(),
                doc.getMana(),
                doc.getMaxMana(),
                doc.getMovement(),
                doc.getMaxMovement(),
                doc.getLevel(),
                doc.getGold(),
                doc.getSilver(),
                doc.getTrust(),
                doc.getInvisLevel(),
                doc.getIncogLevel()
        );
    }
}
