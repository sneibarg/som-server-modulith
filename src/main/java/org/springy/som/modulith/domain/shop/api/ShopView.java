package org.springy.som.modulith.domain.shop.api;

import java.util.List;

public record ShopView(
        String id,
        String areaId,
        String vnum,
        String profitBuy,
        String profitSell,
        String openHour,
        String closeHour,
        String ownerName,
        List<String> tradeItems
) {}
