package org.springy.som.modulith.domain.reset.internal;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import org.springy.som.modulith.domain.area.api.AreaDeletedEvent;

@Component
public class ResetAreaCleanupListener {
    private final ResetRepository resetRepository;

    ResetAreaCleanupListener(ResetRepository resetRepository) {
        this.resetRepository = resetRepository;
    }

    @EventListener
    void onAreaDeleted(AreaDeletedEvent event) {
        resetRepository.deleteAllByAreaId(event.areaId());
    }
}
