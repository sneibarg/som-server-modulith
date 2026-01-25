package org.springy.som.modulith.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.shop.Shop;

import java.util.List;

public interface ShopRepository extends MongoRepository<Shop, String> {
    @Query("{id: '?0'}")
    Shop findShopById(String ShopId);

    @Query("{name: '?0'}")
    List<Shop> findShopsByName(String name);

    @Query("{category: '?0'}")
    List<Shop> findShopsByCategory(String category);

    @Query("{ $or: [ {name: {$regex: ?0, $options: 'i'}}, {category: {$regex: ?0, $options: 'i'}} ] }")
    List<Shop> searchShopsByNameOrCategory(String keyword);

    @Query("{price: {$gte: '?0', $lte: '?1'}}")
    List<Shop> findShopsByPriceRange(double minPrice, double maxPrice);

    @Query("{quantity: {$gte: '?0'}}")
    List<Shop> findShopsWithMinQuantity(int minQuantity);
}
