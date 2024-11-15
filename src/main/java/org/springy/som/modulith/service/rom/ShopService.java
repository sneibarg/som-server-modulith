package org.springy.som.modulith.service.rom;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springy.som.modulith.domain.rom.shop.Shop;
import org.springy.som.modulith.repository.ShopRepository;

import java.util.List;

@Slf4j
@RestController
public class ShopService {
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @PostMapping("/api/v1/shop")
    @ResponseBody
    public Shop createShop(@RequestBody Shop shop) {
        return shopRepository.save(shop);
    }


    @GetMapping(path = "/api/v1/shops")
    @ResponseBody
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @GetMapping(path = "/api/v1/shop")
    @ResponseBody
    public Shop getShop(@RequestParam String shopId) {
        return shopRepository.findShopById(shopId);
    }

    @DeleteMapping(path = "/api/v1/shops")
    @ResponseBody
    public String deleteAll() {
        StringBuilder response = new StringBuilder();
        long shopCount = shopRepository.count();
        response.append("Deleted a total of ").append(shopCount).append(" Shop objects.");
        shopRepository.deleteAll();
        return response.toString();
    }
}
