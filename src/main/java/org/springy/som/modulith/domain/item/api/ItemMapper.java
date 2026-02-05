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
                doc.getDescription(),
                doc.getItemType(),
                doc.getWeight(),
                doc.getExtraFlags(),
                doc.getWearFlags(),
                doc.getValue(),
                doc.getLevel(),
                doc.getAffectData(),
                doc.getExtraDescr()
        );
    }
}
