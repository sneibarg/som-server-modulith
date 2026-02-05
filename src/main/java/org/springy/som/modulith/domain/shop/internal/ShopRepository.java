package org.springy.som.modulith.domain.shop.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ShopRepository extends MongoRepository<ShopDocument, String> {
    @Query("{id: '?0'}")
    ShopDocument findShopById(String ShopId);

    @Query("{name: '?0'}")
    List<ShopDocument> findShopsByName(String name);

    @Query("{category: '?0'}")
    List<ShopDocument> findShopsByCategory(String category);

    @Query("{ $or: [ {name: {$regex: ?0, $options: 'i'}}, {category: {$regex: ?0, $options: 'i'}} ] }")
    List<ShopDocument> searchShopsByNameOrCategory(String keyword);

    @Query("{price: {$gte: '?0', $lte: '?1'}}")
    List<ShopDocument> findShopsByPriceRange(double minPrice, double maxPrice);

    @Query("{quantity: {$gte: '?0'}}")
    List<ShopDocument> findShopsWithMinQuantity(int minQuantity);
}
