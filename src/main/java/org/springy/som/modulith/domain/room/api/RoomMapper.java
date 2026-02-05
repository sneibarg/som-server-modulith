package org.springy.som.modulith.domain.room.api;

import org.springy.som.modulith.domain.room.internal.RoomDocument;

public final class RoomMapper {

    private RoomMapper() {}

    public static RoomView toView(RoomDocument doc) {
        return new RoomView(
                doc.getId(),
                doc.getAreaId(),
                doc.getVnum(),
                doc.getName(),
                doc.getDescription(),
                doc.getExitEast(),
                doc.getExitSouth(),
                doc.getExitNorth(),
                doc.getExitWest(),
                doc.getExitUp(),
                doc.getExitDown(),
                doc.isPvp(),
                doc.isSpawn(),
                doc.getSpawnTimer(),
                doc.getSpawnTime(),
                doc.getTeleDelay(),
                doc.getRoomFlags(),
                doc.getSectorType(),
                doc.getMobiles(),
                doc.getAlternateRoutes(),
                doc.getExtraDescription()
        );
    }
}
