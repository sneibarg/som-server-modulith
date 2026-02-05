package org.springy.som.modulith.domain.item.internal;

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
import org.springy.som.modulith.domain.DeleteAllResponse;
import org.springy.som.modulith.domain.item.api.ItemMapper;
import org.springy.som.modulith.domain.item.api.ItemView;

import java.net.URI;
import java.util.ArrayList;
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
    public ResponseEntity<List<ItemView>> getAllItems() {
        List<ItemView> itemViews = new ArrayList<>();
        for (ItemDocument item : itemService.getAllItems())
            itemViews.add(ItemMapper.toView(item));
        return ResponseEntity.ok(itemViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ItemView> getCommandById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(ItemMapper.toView(itemService.getItemById(id)));
    }

    @PostMapping
    public ResponseEntity<ItemView> createItem(@Valid @RequestBody ItemDocument itemDocument) {
        ItemDocument saved = itemService.createItem(itemDocument);
        ItemView itemView = ItemMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/items/" + saved.getId()))
                .body(itemView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemView> updateItem(@PathVariable String id, @Valid @RequestBody ItemDocument itemDocument) {
        ItemDocument updated = itemService.saveItemForId(id, itemDocument);
        ItemView itemView = ItemMapper.toView(updated);
        return ResponseEntity.ok(itemView);
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
