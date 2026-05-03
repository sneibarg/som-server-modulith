package org.springy.som.modulith.domain.game.api;

import org.springy.som.modulith.domain.game.internal.GameDataDocument;

public final class GameDataMapper {
    private GameDataMapper() {}

    public static GameDataView toView(GameDataDocument doc) {
        if (doc == null) return null;

        return new GameDataView(
                doc.id(),
                doc.kind(),
                doc.status(),
                toView(doc.version()),
                doc.enums(),
                doc.flags(),
                doc.attributeBonuses(),
                doc.classes(),
                doc.races(),
                doc.pcRaces(),
                doc.wiznetTable(),
                doc.groups(),
                doc.titles(),
                doc.itemTable(),
                doc.weapons(),
                doc.attacks(),
                doc.liquids(),
                doc.wellKnownVnums(),
                toView(doc.integrity())
        );
    }

    private static GameDataView.VersionView toView(GameDataDocument.Version v) {
        if (v == null) return null;
        return new GameDataView.VersionView(
                v.family(),
                v.lineage(),
                v.semver(),
                v.createdAt(),
                v.notes()
        );
    }

    private static GameDataView.IntegrityView toView(GameDataDocument.Integrity i) {
        if (i == null) return null;
        return new GameDataView.IntegrityView(
                i.contentHash(),
                toView(i.build())
        );
    }

    private static GameDataView.BuildView toView(GameDataDocument.Build b) {
        if (b == null) return null;
        return new GameDataView.BuildView(
                b.source(),
                b.toolVersion(),
                b.extra()
        );
    }
}
