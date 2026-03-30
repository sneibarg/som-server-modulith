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
                doc.getActFlags(),
                doc.getAffectFlags(),
                doc.getAlignment(),
                doc.getGroup(),
                doc.getAct(),
                doc.getLevel(),
                doc.getHitroll(),
                doc.getHitDiceNumber(),
                doc.getHitDiceType(),
                doc.getHitDiceBonus(),
                doc.getManaDiceNumber(),
                doc.getManaDiceType(),
                doc.getManaDiceBonus(),
                doc.getDamageDiceNumber(),
                doc.getDamageDiceType(),
                doc.getDamageDiceBonus(),
                doc.getDamType(),
                doc.getAcPierce(),
                doc.getAcBash(),
                doc.getAcSlash(),
                doc.getAcExotic(),
                doc.getOffFlags(),
                doc.getImmFlags(),
                doc.getResFlags(),
                doc.getVulnFlags(),
                doc.getStartPos(),
                doc.getDefaultPos(),
                doc.getSex(),
                doc.getGold(),
                doc.getSilver(),
                doc.getPulseWait(),
                doc.getPulseDaze(),
                doc.getForm(),
                doc.getParts(),
                doc.getSize(),
                doc.getMaterial(),
                doc.getFlags()
        );
    }
}
