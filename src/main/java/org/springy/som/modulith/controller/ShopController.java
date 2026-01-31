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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.service.DeleteAllResponse;
import org.springy.som.modulith.service.ShopService;
import org.springy.som.modulith.domain.shop.Shop;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/shops", produces = "application/json")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<List<Shop>> getResets() {
        return ResponseEntity.ok(shopService.getAllShops());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Shop> getResetById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(shopService.getShopById(id));
    }

    @PostMapping
    public ResponseEntity<Shop> createReset(@Valid @RequestBody Shop shop) {
        Shop saved = shopService.createShop(shop);
        return ResponseEntity
                .created(URI.create("/api/v1/resets/" + saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateReset(@PathVariable String id, @Valid @RequestBody Shop shop) {
        Shop updated = shopService.saveShopForId(id, shop);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResetsById(@PathVariable String id) {
        shopService.deleteShopById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(shopService.deleteAllShops()));
    }
}
