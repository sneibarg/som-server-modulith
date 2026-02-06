package org.springy.som.modulith.domain.race.api;

import org.springy.som.modulith.domain.race.internal.RaceDocument;

import java.util.List;

public interface RaceApi {
    List<RaceDocument> getAllRaces();
    RaceDocument getRaceByName(String name);
    RaceDocument getRaceById(String id);
    RaceDocument createRace(RaceDocument raceDocument);
    RaceDocument saveRaceForId(String id, RaceDocument raceDocument);
    void deleteRaceById(String id);
    long deleteAllRaces();
}
