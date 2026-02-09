package org.springy.som.modulith.domain.mobile.internal;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springy.som.modulith.domain.area.api.AreaDeletedEvent;

@Component
public class MobileAreaCleanupListener {
    private final MobileRepository mobileRepository;

    MobileAreaCleanupListener(MobileRepository mobileRepository) {
        this.mobileRepository = mobileRepository;
    }

    @Async
    @EventListener
    void onAreaDeleted(AreaDeletedEvent event) {
        mobileRepository.deleteAllByAreaId(event.areaId());
    }
}
