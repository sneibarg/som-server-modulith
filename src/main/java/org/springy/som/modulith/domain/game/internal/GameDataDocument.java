package org.springy.som.modulith.domain.game.internal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document("GameData")
public record GameDataDocument(
        @Id String id,
        String kind,
        String status,
        Version version,
        Constants constants,
        Map<String, List<String>> enums,
        Map<String, List<String>> flagDomains,
        Catalogs catalogs,
        Map<String, Map<String, Integer>> wellKnownVnums,
        Integrity integrity) {

    public record Version(
            String family,
            List<String> lineage,
            String semver,
            Instant createdAt,
            String notes
    ) {}

    public record Constants(
            Map<String, Integer> max,
            Map<String, Integer> pulses
    ) {}

    public record Catalogs(
            Catalog classes,
            Catalog races,
            Catalog pcRaces,
            Catalog skills,
            Catalog groups,
            Catalog weapons,
            Catalog attacks,
            Catalog liquids
    ) {}

    public record Catalog(
            Map<String, Map<String, Object>> byId,
            Map<String, String> byName
    ) {}

    public record Integrity(
            String contentHash,
            Build build
    ) {}

    public record Build(
            String source,
            String toolVersion
    ) {}
}
