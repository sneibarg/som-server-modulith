package org.springy.som.modulith.domain.player.api;

import org.springy.som.modulith.domain.player.internal.PlayerDocument;

import java.util.List;

public interface PlayerApi {
    List<PlayerDocument> getAllPlayerAccounts();
    PlayerDocument getPlayerAccountByName(String accountName);
    PlayerDocument getPlayerAccountById(String id);
    PlayerDocument createPlayerAccount(PlayerDocument playerDocument);
    PlayerDocument savePlayerAccountForId(String id, PlayerDocument playerDocument);
    void deletePlayerAccountById(String id);
    long deleteAllPlayerAccounts();
}
