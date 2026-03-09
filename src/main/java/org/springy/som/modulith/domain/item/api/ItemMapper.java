package org.springy.som.modulith.domain.item.api;

import org.springy.som.modulith.domain.item.internal.ItemDocument;

public final class ItemMapper {

    private ItemMapper() {}

    public static ItemView toView(ItemDocument doc) {
        return new ItemView(
                doc.getId(),
                doc.getAreaId(),
                doc.getVnum(),
                doc.getName(),
                doc.getShortDescription(),
                doc.getLongDescription(),
                doc.getItemType(),
                doc.getExtraFlags(),
                doc.getWearFlags(),
                doc.getValue(),
                doc.getLevel(),
                doc.getWeight(),
                doc.getCost(),
                doc.getCondition(),
                doc.getValue0(),
                doc.getValue1(),
                doc.getValue2(),
                doc.getValue3(),
                doc.getValue4(),
                doc.getAffectData(),
                doc.getExtraDescr()
        );
    }
}
