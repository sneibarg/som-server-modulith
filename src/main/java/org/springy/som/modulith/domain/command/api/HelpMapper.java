package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.HelpDocument;

public final class HelpMapper {

    private HelpMapper() {}

    public static HelpView toView(HelpDocument doc) {
        return new HelpView(
            doc.getId(),
            doc.getLevel(),
            doc.getKeyword(),
            doc.getText()
        );
    }
}
