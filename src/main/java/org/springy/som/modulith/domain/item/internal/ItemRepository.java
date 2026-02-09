package org.springy.som.modulith.domain.item.internal;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<ItemDocument, String> {
    @Query("{id: '?0'}")
    ItemDocument findItemById(String itemId);

    @Query("{name: '?0'}")
    List<ItemDocument> findItemsByName(String name);

    @Query("{category: '?0'}")
    List<ItemDocument> findItemsByCategory(String category);

    @Query("{ $or: [ {name: {$regex: ?0, $options: 'i'}}, {category: {$regex: ?0, $options: 'i'}} ] }")
    List<ItemDocument> searchItemsByNameOrCategory(String keyword);

    @Query("{price: {$gte: '?0', $lte: '?1'}}")
    List<ItemDocument> findItemsByPriceRange(double minPrice, double maxPrice);

    @Query("{quantity: {$gte: '?0'}}")
    List<ItemDocument> findItemsWithMinQuantity(int minQuantity);

    long deleteAllByAreaId(String areaId);
}
