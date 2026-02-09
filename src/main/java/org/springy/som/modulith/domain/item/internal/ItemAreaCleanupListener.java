package org.springy.som.modulith.domain.item.internal;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import org.springy.som.modulith.domain.area.api.AreaDeletedEvent;

@Component
public class ItemAreaCleanupListener {
    private final ItemRepository itemRepository;

    ItemAreaCleanupListener(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Async
    @EventListener
    void onAreaDeleted(AreaDeletedEvent event) {
        itemRepository.deleteAllByAreaId(event.areaId());
    }
}
