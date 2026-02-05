package org.springy.som.modulith.domain.area.api;

import org.springy.som.modulith.domain.area.internal.AreaDocument;

public final class AreaMapper {

    private AreaMapper() {}

    public static AreaView toView(AreaDocument doc) {
        return new AreaView(
                doc.getId(),
                doc.getAuthor(),
                doc.getName(),
                doc.getSuggestedLevelRange(),
                doc.getRooms(),
                doc.getMobiles(),
                doc.getObjects(),
                doc.getShops(),
                doc.getResets(),
                doc.getSpecials()
        );
    }
}
