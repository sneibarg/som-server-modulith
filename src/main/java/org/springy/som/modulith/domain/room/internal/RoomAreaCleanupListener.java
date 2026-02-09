package org.springy.som.modulith.domain.room.internal;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.springy.som.modulith.domain.area.api.AreaDeletedEvent;

@Component
public class RoomAreaCleanupListener {
    private final RoomRepository roomRepository;

    RoomAreaCleanupListener(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @EventListener
    void onAreaDeleted(AreaDeletedEvent event) {
        roomRepository.deleteAllByAreaId(event.areaId());
    }
}
