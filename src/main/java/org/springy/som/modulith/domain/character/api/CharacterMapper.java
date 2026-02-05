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
                doc.getCloaked(),
                doc.getInventory(),
                doc.getHealth(),
                doc.getMana(),
                doc.getMovement(),
                doc.getLevel(),
                doc.getExperience(),
                doc.getAccumulatedExperience(),
                doc.getTrains(),
                doc.getPractices(),
                doc.getGold(),
                doc.getSilver(),
                doc.getWimpy(),
                doc.getPosition(),
                doc.getMaxWeight(),
                doc.getMaxItems(),
                doc.getReputation(),
                doc.getPiercing(),
                doc.getBashing(),
                doc.getSlashing(),
                doc.getMagic()
        );
    }
}
