package org.springy.som.modulith.domain.item.api;

import org.springy.som.modulith.domain.item.internal.ItemDocument;

import java.util.List;

public interface ItemApi {
    List<ItemDocument> getAllItems();
    ItemDocument getItemByName(String name);
    ItemDocument getItemById(String id);
    ItemDocument createItem(ItemDocument itemDocument);
    ItemDocument saveItemForId(String id, ItemDocument itemDocument);
    void deleteItemById(String id);
    long deleteAllItems();
}
