package org.springy.som.modulith.domain.mobile.api;

import org.springy.som.modulith.domain.mobile.internal.MobileDocument;

public final class MobileMapper {

    private MobileMapper() {}

    public static MobileView toView(MobileDocument doc) {
        return new MobileView(
                doc.getId(),
                doc.getAreaId(),
                doc.getVnum(),
                doc.getName(),
                doc.getShortDescription(),
                doc.getLongDescription(),
                doc.getDescription(),
                doc.getRace(),
                doc.getAlignment(),
                doc.getGroup(),
                doc.getDamType(),
                doc.getHitDice(),
                doc.getManaDice(),
                doc.getDamageDice(),
                doc.getArmorClass(),
                doc.getStartPos(),
                doc.getDefaultPos(),
                doc.getSex(),
                doc.getSize(),
                doc.getMaterial(),
                doc.getFlags(),
                doc.getStatusFlags(),
                doc.getLevel(),
                doc.getHitroll(),
                doc.getGold(),
                doc.getSilver()
        );
    }
}
