package org.springy.som.modulith.domain.shop.api;

import org.springy.som.modulith.domain.shop.internal.ShopDocument;

public final class ShopMapper {

    private ShopMapper() {}

    public static ShopView toView(ShopDocument doc) {
        return new ShopView(
                doc.getId(),
                doc.getAreaId(),
                doc.getKeeper(),
                doc.getBuyType0(),
                doc.getBuyType1(),
                doc.getBuyType2(),
                doc.getBuyType3(),
                doc.getBuyType4(),
                doc.getProfitBuy(),
                doc.getProfitSell(),
                doc.getOpenHour(),
                doc.getCloseHour(),
                doc.getComment()
        );
    }
}
