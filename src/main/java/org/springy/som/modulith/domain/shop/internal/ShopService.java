package org.springy.som.modulith.domain.shop.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.shopIdMissing;
import static org.springy.som.modulith.util.DomainGuards.shopMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

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
    public List<ShopDocument> getAllShops() {
        return shopRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ShopDocument getShopByName(@RequestParam String name) {
        return shopRepository.findShopById(name);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ShopDocument getShopById(@RequestParam String itemId) {
        return shopRepository.findShopById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ShopDocument createShop(@Valid @RequestBody ShopDocument shopDocument) {
        requireEntityWithId(shopDocument, ShopDocument::getId, shopMissing(), shopIdMissing());

        try {
            // if (shopRepository.existsById(shopDocument.getId())) throw new ShopConflictException(...)
            return shopRepository.save(shopDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createShop shopId={}", safeId(shopDocument, ShopDocument::getId), ex);
            throw new ShopPersistenceException("Failed to create ROM shopDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ShopDocument saveShopForId(String id, ShopDocument shopDocument) {
        requireText(id, shopIdMissing());
        requireEntityWithId(shopDocument, ShopDocument::getId, shopMissing(), shopIdMissing());

        return shopRepository.save(getShopById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteShopById(String id) {
        requireText(id, shopIdMissing());

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

    private List<ShopDocument> getAllShopsFallback(Throwable t) {
        log.warn("Fallback getAllShops due to {}", t.toString());
        return List.of();
    }

    private ShopDocument getShopByIdFallback(String id, Throwable t) {
        log.warn("Fallback getShopById id={} due to {}", id, t.toString());
        throw new ShopPersistenceException("ROM shop lookup temporarily unavailable: " + id+" "+t);
    }
}
