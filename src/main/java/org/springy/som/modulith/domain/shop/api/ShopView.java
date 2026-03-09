package org.springy.som.modulith.domain.shop.api;

public record ShopView(
        String id,
        String areaId,
        String keeper,
        String buyType0,
        String buyType1,
        String buyType2,
        String buyType3,
        String buyType4,
        String profitBuy,
        String profitSell,
        String openHour,
        String closeHour,
        String comment
) {}
