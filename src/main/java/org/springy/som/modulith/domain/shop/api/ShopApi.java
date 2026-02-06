package org.springy.som.modulith.domain.shop.api;

import org.springy.som.modulith.domain.shop.internal.ShopDocument;

import java.util.List;

public interface ShopApi {
    List<ShopDocument> getAllShops();
    ShopDocument getShopByName(String name);
    ShopDocument getShopById(String id);
    ShopDocument createShop(ShopDocument shopDocument);
    ShopDocument saveShopForId(String id, ShopDocument shopDocument);
    void deleteShopById(String id);
    long deleteAllShops();
}
