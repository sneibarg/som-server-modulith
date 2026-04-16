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
                doc.getRace(),
                doc.getSex(),
                doc.getCharacterClass(),
                doc.getRoomId(),
                doc.getAreaId(),
                doc.getGuild(),
                doc.getRole(),
                doc.getCharacterFlags(),
                doc.getArmorClass(),
                doc.getCharacterAttributes(),
                doc.getTemporalMechanics(),
                doc.getCloaked(),
                doc.getInventory(),
                doc.getPromptFormat(),
                doc.getHit(),
                doc.getMaxHit(),
                doc.getMana(),
                doc.getMaxMana(),
                doc.getMovement(),
                doc.getMaxMovement(),
                doc.getLevel(),
                doc.getExperience(),
                doc.getAccumulatedExperience(),
                doc.getGold(),
                doc.getSilver(),
                doc.getTrust()
        );
    }
}
