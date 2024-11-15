package org.springy.som.modulith.service.rom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.item.Item;
import org.springy.som.modulith.repository.ItemRepository;

import java.util.List;

@Slf4j
@RestController
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @PostMapping("/api/v1/item")
    @ResponseBody
    public Item createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }


    @GetMapping(path = "/api/v1/items")
    @ResponseBody
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping(path = "/api/v1/item")
    @ResponseBody
    public Item getItem(@RequestParam String itemId) {
        return itemRepository.findItemById(itemId);
    }

    @DeleteMapping(path = "/api/v1/items")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long itemCount = itemRepository.count();
        response.append("Deleted a total of ").append(itemCount).append(" Item objects.");
        itemRepository.deleteAll();
        return response.toString();
    }
}
