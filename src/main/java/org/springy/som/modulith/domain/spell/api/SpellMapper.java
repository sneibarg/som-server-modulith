package org.springy.som.modulith.domain.spell.api;

import org.springy.som.modulith.domain.spell.internal.SpellDocument;

public final class SpellMapper {

    private SpellMapper() {}

    public static SpellView toView(SpellDocument doc) {
        return new SpellView(
            doc.getId(),
            doc.getName(),
            doc.getKind(),
            doc.getHandlerId(),
            doc.getTarget(),
            doc.getMinPosition(),
            doc.getNounDamage(),
            doc.getFunctionName(),
            doc.getGuards(),
            doc.getPayload(),
            doc.getLevelByClass(),
            doc.getRatingByClass(),
            doc.getSlot(),
            doc.getMinMana(),
            doc.getBeats(),
            doc.getAffectData(),
            doc.getLambdas()
        );
    }
}
