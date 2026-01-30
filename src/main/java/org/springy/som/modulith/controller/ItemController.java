package org.springy.som.modulith.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.ItemService;
import org.springy.som.modulith.domain.item.Item;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/items", produces = "application/json")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Item> getCommandById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@Valid @RequestBody Item item) {
        Item saved = itemService.createItem(item);
        return ResponseEntity
                .created(URI.create("/api/v1/items/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @Valid @RequestBody Item item) {
        Item updated = itemService.saveItemForId(id, item);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerCharacterById(@PathVariable String id) {
        itemService.deleteItemById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(itemService.deleteAllItems()));
    }
}
