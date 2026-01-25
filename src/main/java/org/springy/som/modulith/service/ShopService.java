package org.springy.som.modulith.service;

import org.springframework.stereotype.Service;
import org.springy.som.modulith.domain.shop.Shop;
import org.springy.som.modulith.repository.ShopRepository;
import java.util.List;

@Service
public class ShopService {
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Shop getShopById(String shopId) {
        return shopRepository.findShopById(shopId);
    }

    public Shop saveShop(Shop shop) {
        return shopRepository.save(shop);
    }

    public long deleteAllShops() {
        long shopCount =  shopRepository.count();
        shopRepository.deleteAll();
        return shopCount;
    }
}
