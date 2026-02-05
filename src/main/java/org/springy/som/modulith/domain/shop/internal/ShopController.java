package org.springy.som.modulith.domain.shop.internal;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.DeleteAllResponse;
import org.springy.som.modulith.domain.shop.api.ShopMapper;
import org.springy.som.modulith.domain.shop.api.ShopView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/shops", produces = "application/json")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @GetMapping
    public ResponseEntity<List<ShopView>> getResets() {
        List<ShopView> shopViews = new ArrayList<>();
        for (ShopDocument shopDocument : shopService.getAllShops())
            shopViews.add(ShopMapper.toView(shopDocument));
        return ResponseEntity.ok(shopViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ShopView> getResetById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(ShopMapper.toView(shopService.getShopById(id)));
    }

    @PostMapping
    public ResponseEntity<ShopView> createReset(@Valid @RequestBody ShopDocument shopDocument) {
        ShopDocument saved = shopService.createShop(shopDocument);
        ShopView shopView = ShopMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/resets/" + saved.getId()))
                .body(shopView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopView> updateReset(@PathVariable String id, @Valid @RequestBody ShopDocument shopDocument) {
        ShopDocument updated = shopService.saveShopForId(id, shopDocument);
        ShopView shopView = ShopMapper.toView(updated);
        return ResponseEntity.ok(shopView);
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
