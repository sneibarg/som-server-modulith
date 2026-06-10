package org.springy.som.modulith.domain.skill.api;

import org.springy.som.modulith.domain.skill.internal.SkillDocument;

public final class SkillMapper {

    private SkillMapper() {}

    public static SkillView toView(SkillDocument doc) {
        return new SkillView(
            doc.getId(),
            doc.getName(),
            doc.getKind(),
            doc.getHandlerId(),
            doc.getTarget(),
            doc.getMinPosition(),
            doc.getNounDamage(),
            doc.getFightExecutor(),
            doc.getPayload(),
            doc.getGuards(),
            doc.getFightPlan(),
            doc.getLevelByClass(),
            doc.getRatingByClass(),
            doc.getSlot(),
            doc.getMinMana(),
            doc.getBeats()
        );
    }
}
