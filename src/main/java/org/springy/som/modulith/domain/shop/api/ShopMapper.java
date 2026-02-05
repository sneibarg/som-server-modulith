package org.springy.som.modulith.domain.shop.api;

import org.springy.som.modulith.domain.shop.internal.ShopDocument;

public final class ShopMapper {

    private ShopMapper() {}

    public static ShopView toView(ShopDocument doc) {
        return new ShopView(
                doc.getId(),
                doc.getAreaId(),
                doc.getVnum(),
                doc.getProfitBuy(),
                doc.getProfitSell(),
                doc.getOpenHour(),
                doc.getCloseHour(),
                doc.getOwnerName(),
                doc.getTradeItems()
        );
    }
}
