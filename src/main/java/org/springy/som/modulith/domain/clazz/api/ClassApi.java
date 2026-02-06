package org.springy.som.modulith.domain.clazz.api;

import org.springy.som.modulith.domain.clazz.internal.RomClassDocument;

import java.util.List;

public interface ClassApi {
    List<RomClassDocument> getAllClasses();
    RomClassDocument getRomClassById(String id);
    RomClassDocument createRomClass(RomClassDocument romClassDocument);
    RomClassDocument saveRomClassForId(String id, RomClassDocument romClassDocument);
    void deleteRomClassById(String id);
    long deleteAllRomClasses();
}
