package org.springy.som.modulith.domain.special.api;


import org.springy.som.modulith.domain.special.internal.SpecialDocument;

import java.util.List;

public interface SpecialApi {
    List<SpecialDocument> getAllSpecials();
    SpecialDocument getSpecialById(String id);
    SpecialDocument getSpecialByName(String name);
    SpecialDocument createSpecial(SpecialDocument specialDocument);
    SpecialDocument saveSpecialForId(String id, SpecialDocument specialDocument);
    void deleteSpecialById(String id);
    long deleteAllSpecials();
}
