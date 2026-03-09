package org.springy.som.modulith.domain.reset.api;

import org.springy.som.modulith.domain.reset.internal.ResetDocument;

public final class ResetMapper {

    private ResetMapper() {}

    public static ResetView toView(ResetDocument doc) {
        return new ResetView(
                doc.getId(),
                doc.getAreaId(),
                doc.getCommand(),
                doc.getComment(),
                doc.getArg1(),
                doc.getArg2(),
                doc.getArg3(),
                doc.getArg4()
        );
    }
}
