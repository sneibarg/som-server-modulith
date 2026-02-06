package org.springy.som.modulith.domain.mobile.api;

import org.springy.som.modulith.domain.mobile.internal.MobileDocument;

import java.util.List;

public interface MobileApi {
    List<MobileDocument> getAllMobiles();
    MobileDocument getMobileByName(String mobileName);
    MobileDocument getMobileById(String id);
    MobileDocument createMobile(MobileDocument mobileDocument);
    MobileDocument saveMobileForId(String id, MobileDocument mobileDocument);
    void deleteMobileById(String id);
    long deleteAllMobiles();
}
