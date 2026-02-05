package org.springy.som.modulith.domain.game.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record GameDataView(
        String id,
        String kind,
        String status,
        VersionView version,
        ConstantsView constants,
        Map<String, List<String>> enums,
        Map<String, List<String>> flagDomains,
        CatalogsView catalogs,
        Map<String, Map<String, Integer>> wellKnownVnums,
        IntegrityView integrity
) {
    public record VersionView(
            String family,
            List<String> lineage,
            String semver,
            Instant createdAt,
            String notes
    ) {}

    public record ConstantsView(
            Map<String, Integer> max,
            Map<String, Integer> pulses
    ) {}

    public record CatalogsView(
            CatalogView classes,
            CatalogView races,
            CatalogView pcRaces,
            CatalogView skills,
            CatalogView groups,
            CatalogView weapons,
            CatalogView attacks,
            CatalogView liquids
    ) {}

    public record CatalogView(
            Map<String, Map<String, Object>> byId,
            Map<String, String> byName
    ) {}

    public record IntegrityView(
            String contentHash,
            BuildView build
    ) {}

    public record BuildView(
            String source,
            String toolVersion
    ) {}
}
