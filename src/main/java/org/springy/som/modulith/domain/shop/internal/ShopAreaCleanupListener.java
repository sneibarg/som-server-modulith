package org.springy.som.modulith.domain.shop.internal;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springy.som.modulith.domain.area.api.AreaDeletedEvent;

@Component
public class ShopAreaCleanupListener {
    private final ShopRepository shopRepository;

    ShopAreaCleanupListener(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Async
    @EventListener
    void onAreaDeleted(AreaDeletedEvent event) {
        shopRepository.deleteAllByAreaId(event.areaId());
    }
}
