package org.springy.som.modulith.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.item.Item;
import org.springy.som.modulith.repository.ItemRepository;

import java.util.List;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(String itemId) {
        return itemRepository.findById(itemId).orElse(null);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public long deleteAllItems() {
        long itemCount = itemRepository.count();
        itemRepository.deleteAll();
        return itemCount;
    }
}
