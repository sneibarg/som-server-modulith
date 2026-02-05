package org.springy.som.modulith.domain.race.api;

import org.springy.som.modulith.domain.race.internal.RomRaceDocument;

public final class RomRaceMapper {

    private RomRaceMapper() {}

    public static RomRaceView toView(RomRaceDocument doc) {
        return new RomRaceView(
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
