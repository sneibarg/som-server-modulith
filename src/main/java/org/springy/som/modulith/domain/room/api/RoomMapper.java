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
                doc.getExtraDescription(),
                doc.isPvp(),
                doc.isSpawn(),
                doc.getSpawnTimer(),
                doc.getSpawnTime(),
                doc.getTeleDelay(),
                doc.getRoomFlags(),
                doc.getSectorType(),
                doc.getExits(),
                doc.getMobiles()
        );
    }
}
