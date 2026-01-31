package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.shop.Shop;
import org.springy.som.modulith.exception.shop.InvalidShopException;
import org.springy.som.modulith.exception.shop.ShopNotFoundException;
import org.springy.som.modulith.exception.shop.ShopPersistenceException;
import org.springy.som.modulith.repository.ShopRepository;
import java.util.List;

@Service
@Slf4j
public class ShopService {
    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllShopsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Shop getShopByName(@RequestParam String name) {
        return shopRepository.findShopById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Shop getShopById(@RequestParam String itemId) {
        return shopRepository.findShopById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Shop createShop(@Valid @RequestBody Shop shop) {
        requireShop(shop);

        try {
            // if (shopRepository.existsById(shop.getShopById())) throw new ShopConflictException(...)
            return shopRepository.save(shop);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createShop areaId={}", safeId(shop), ex);
            throw new ShopPersistenceException("Failed to create ROM shop"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Shop saveShopForId(String id, Shop shop) {
        requireId(id);
        requireShop(shop);

        return shopRepository.save(getShopById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteShopById(String id) {
        requireId(id);

        try {
            if (!shopRepository.existsById(id)) {
                throw new ShopNotFoundException(id);
            }
            shopRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteShopById id={}", id, ex);
            throw new ShopPersistenceException("Failed to delete ROM shop: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllShops() {
        try {
            long itemCount = shopRepository.count();
            shopRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllShops", ex);
            throw new ShopPersistenceException("Failed to delete all ROM rooms "+ ex);
        }
    }

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidShopException("ROM shop id must be provided");
        }
    }

    private static void requireShop(Shop shop) {
        if (shop == null) {
            throw new InvalidShopException("ROM shop must be provided");
        }

        requireId(shop.getId());
    }

    private static String safeId(Shop shop) {
        try {
            return shop.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<Shop> getAllShopFallback(Throwable t) {
        log.warn("Fallback getAllShops due to {}", t.toString());
        return List.of();
    }

    private Shop getRoomsByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllShopsById id={} due to {}", id, t.toString());
        throw new ShopPersistenceException("ROM shop lookup temporarily unavailable: " + id+" "+t);
    }
}
