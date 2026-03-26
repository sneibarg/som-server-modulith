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
        Map<String, Map<String, Integer>> flags,
        Map<String, Map<String, Object>> classes,
        Map<String, Map<String, Object>> races,
        Map<String, Map<String, Object>> pcRaces,
        Map<String, Map<String, Object>> skills,
        Map<String, Map<String, Object>> groups,
        Map<String, Map<String, Object>> weapons,
        Map<String, Map<String, Object>> attacks,
        Map<String, Map<String, Object>> liquids,
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

    public record Integrity(
            String contentHash,
            Build build
    ) {}

    public record Build(
            String source,
            String toolVersion,
            Map<String, Object> extra
    ) {}
}
