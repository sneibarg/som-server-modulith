package org.springy.som.modulith.domain.race.api;

import org.springy.som.modulith.domain.race.internal.RaceDocument;

public final class RaceMapper {

    private RaceMapper() {}

    public static RaceView toView(RaceDocument doc) {
        return new RaceView(
                doc.getId(),
                doc.getName(),
                doc.getSize(),
                doc.getSkills(),
                doc.getClassMultiplier(),
                doc.getPoints(),
                doc.getStr(),
                doc.getMaxStr(),
                doc.getINT(),
                doc.getMaxInt(),
                doc.getCon(),
                doc.getMaxCon(),
                doc.getWis(),
                doc.getMaxWis(),
                doc.getDex(),
                doc.getMaxDex()
        );
    }
}
