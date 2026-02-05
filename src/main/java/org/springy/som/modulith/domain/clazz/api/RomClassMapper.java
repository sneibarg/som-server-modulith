package org.springy.som.modulith.domain.clazz.api;

import org.springy.som.modulith.domain.clazz.internal.RomClassDocument;

public final class RomClassMapper {

    private RomClassMapper() {}

    public static RomClassView toView(RomClassDocument doc) {
        return new RomClassView(
                doc.getId(),
                doc.getName(),
                doc.getWhoName(),
                doc.getPrimaryAttribute(),
                doc.getStartingWeapon(),
                doc.getBaseGroup(),
                doc.getDefaultGroup(),
                doc.getSkillAdept(),
                doc.getThac0_00(),
                doc.getThac0_32(),
                doc.getHpMin(),
                doc.getHpMax(),
                doc.isFMana()
        );
    }
}
