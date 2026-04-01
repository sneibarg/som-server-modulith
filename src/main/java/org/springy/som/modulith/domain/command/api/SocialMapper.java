package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.SocialDocument;

public final class SocialMapper {

    private SocialMapper() {}

    public static SocialView toView(SocialDocument doc) {
        return new SocialView(
            doc.getId(),
            doc.getName(),
            doc.getCharNoArg(),
            doc.getOthersNoArg(),
            doc.getCharFound(),
            doc.getOthersFound(),
            doc.getVictFound(),
            doc.getCharNotFound(),
            doc.getCharAuto(),
            doc.getOthersAuto()
        );
    }
}
