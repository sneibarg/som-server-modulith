package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.ItemService;
import org.springy.som.modulith.domain.item.Item;

import java.util.List;

@RestController
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/api/v1/item")
    @ResponseBody
    public Item createItem(@RequestBody Item item) {
        return itemService.saveItem(item);
    }

    @GetMapping(path = "/api/v1/items")
    @ResponseBody
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping(path = "/api/v1/item")
    @ResponseBody
    public Item getItem(@RequestParam String itemId) {
        return itemService.getItemById(itemId);
    }

    @DeleteMapping(path = "/api/v1/items")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(itemService.deleteAllItems())
                .append(" Item objects.");
        return response.toString();
    }
}
