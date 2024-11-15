package org.springy.som.modulith.repository.rom;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springy.som.modulith.domain.rom.item.Item;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
    @Query("{id: '?0'}")
    Item findItemById(String itemId);

    @Query("{name: '?0'}")
    List<Item> findItemsByName(String name);

    @Query("{category: '?0'}")
    List<Item> findItemsByCategory(String category);

    @Query("{ $or: [ {name: {$regex: ?0, $options: 'i'}}, {category: {$regex: ?0, $options: 'i'}} ] }")
    List<Item> searchItemsByNameOrCategory(String keyword);

    @Query("{price: {$gte: '?0', $lte: '?1'}}")
    List<Item> findItemsByPriceRange(double minPrice, double maxPrice);

    @Query("{quantity: {$gte: '?0'}}")
    List<Item> findItemsWithMinQuantity(int minQuantity);
}
