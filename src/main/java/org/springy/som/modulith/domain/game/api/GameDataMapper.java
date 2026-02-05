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
                toView(doc.constants()),
                doc.enums(),
                doc.flagDomains(),
                toView(doc.catalogs()),
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

    private static GameDataView.ConstantsView toView(GameDataDocument.Constants c) {
        if (c == null) return null;
        return new GameDataView.ConstantsView(
                c.max(),
                c.pulses()
        );
    }

    private static GameDataView.CatalogsView toView(GameDataDocument.Catalogs c) {
        if (c == null) return null;
        return new GameDataView.CatalogsView(
                toView(c.classes()),
                toView(c.races()),
                toView(c.pcRaces()),
                toView(c.skills()),
                toView(c.groups()),
                toView(c.weapons()),
                toView(c.attacks()),
                toView(c.liquids())
        );
    }

    private static GameDataView.CatalogView toView(GameDataDocument.Catalog c) {
        if (c == null) return null;
        return new GameDataView.CatalogView(
                c.byId(),
                c.byName()
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
                b.toolVersion()
        );
    }
}
