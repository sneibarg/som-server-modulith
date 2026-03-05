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
        Map<String, Map<String, Integer>> flags,
        Map<String, Map<String, Object>> classes,
        Map<String, Map<String, Object>> races,
        Map<String, Map<String, Object>> pcRaces,
        Map<String, Map<String, Object>> skills,
        Map<String, Map<String, Object>> groups,
        Map<String, Map<String, Object>> weapons,
        Map<String, Map<String, Object>> attacks,
        Map<String, Map<String, Object>> liquids,
        Map<String, String> classesNameIndex,
        Map<String, String> racesNameIndex,
        Map<String, String> pcRacesNameIndex,
        Map<String, String> skillsNameIndex,
        Map<String, String> groupsNameIndex,
        Map<String, String> weaponsNameIndex,
        Map<String, String> attacksNameIndex,
        Map<String, String> liquidsNameIndex,
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

    public record IntegrityView(
            String contentHash,
            BuildView build
    ) {}

    public record BuildView(
            String source,
            String toolVersion,
            Map<String, Object> extra
    ) {}
}
