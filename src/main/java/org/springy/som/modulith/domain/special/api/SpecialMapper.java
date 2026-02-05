package org.springy.som.modulith.domain.special.api;

import org.springy.som.modulith.domain.special.internal.SpecialDocument;

public final class SpecialMapper {

    private SpecialMapper() {}

    public static SpecialView toView(SpecialDocument doc) {
        return new SpecialView(
                doc.getId(),
                doc.getAreaId(),
                doc.getMobVnum(),
                doc.getSpecialFunction(),
                doc.getComment()
        );
    }
}
