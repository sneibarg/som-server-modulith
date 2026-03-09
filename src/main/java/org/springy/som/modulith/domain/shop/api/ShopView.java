package org.springy.som.modulith.domain.shop.api;

public record ShopView(
        String id,
        String areaId,
        int keeper,
        int buyType0,
        int buyType1,
        int buyType2,
        int buyType3,
        int buyType4,
        int profitBuy,
        int profitSell,
        int openHour,
        int closeHour,
        String comment
) {}
