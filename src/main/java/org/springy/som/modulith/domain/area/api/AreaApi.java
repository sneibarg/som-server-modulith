package org.springy.som.modulith.domain.area.api;

import org.springy.som.modulith.domain.area.internal.AreaDocument;

import java.util.List;

public interface AreaApi {
    List<AreaDocument> getAllAreas();
    AreaDocument getAreaById(String id);
    AreaDocument createArea(AreaDocument areaDocument);
    AreaDocument saveAreaForId(String id, AreaDocument areaDocument);
    void deleteAreaById(String id);
    long deleteAllAreas();
}
