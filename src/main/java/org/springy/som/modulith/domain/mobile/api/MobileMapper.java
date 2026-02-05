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
                doc.getActFlags(),
                doc.getAffectFlags(),
                doc.getAlignment(),
                doc.getRace(),
                doc.getSex(),
                doc.getStartPos(),
                doc.getDefaultPos(),
                doc.getHitroll(),
                doc.getDamage(),
                doc.getLevel(),
                doc.getGold()
        );
    }
}
