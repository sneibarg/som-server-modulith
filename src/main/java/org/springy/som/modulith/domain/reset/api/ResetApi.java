package org.springy.som.modulith.domain.reset.api;

import org.springy.som.modulith.domain.reset.internal.ResetDocument;

import java.util.List;

public interface ResetApi {
    List<ResetDocument> getAllResets();
    ResetDocument getResetByName(String name);
    ResetDocument getResetById(String id);
    ResetDocument createReset(ResetDocument resetDocument);
    ResetDocument saveResetForId(String id, ResetDocument resetDocument);
    void deleteResetById(String id);
    long deleteAllResets();
}
