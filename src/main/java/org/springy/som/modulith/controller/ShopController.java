package org.springy.som.modulith.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.service.ShopService;
import org.springy.som.modulith.domain.shop.Shop;

import java.util.List;

@RestController
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/api/v1/shop")
    @ResponseBody
    public Shop createShop(@RequestBody Shop shop) {
        return shopService.saveShop(shop);
    }

    @GetMapping(path = "/api/v1/shops")
    @ResponseBody
    public List<Shop> getAllShops() {
        return shopService.getAllShops();
    }

    @GetMapping(path = "/api/v1/shop")
    @ResponseBody
    public Shop getShop(@RequestParam String shopId) {
        return shopService.getShopById(shopId);
    }

    @DeleteMapping(path = "/api/v1/shops")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        response.append("Deleted a total of ")
                .append(shopService.deleteAllShops())
                .append(" Shop objects.");
        return response.toString();
    }
}
