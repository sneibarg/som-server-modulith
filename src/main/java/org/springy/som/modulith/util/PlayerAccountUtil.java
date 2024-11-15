package org.springy.som.modulith.util;

import org.bson.BsonDocument;
import org.bson.json.JsonObject;
import org.springy.som.modulith.domain.rom.player.PlayerAccount;

import java.util.ArrayList;
import java.util.List;

public class PlayerAccountUtil {
    public static PlayerAccount getPlayerFromJson(String playerJson) {
        PlayerAccount newPlayerAccount = new PlayerAccount();
        JsonObject json = new JsonObject(playerJson);
        BsonDocument document = json.toBsonDocument();
        List<String> playerCharacterList = new ArrayList<>();

        newPlayerAccount.setAccountName(document.get("accountName").asString().getValue());
        newPlayerAccount.setFirstName(document.get("firstName").asString().getValue());
        newPlayerAccount.setLastName(document.get("lastName").asString().getValue());
        newPlayerAccount.setEmailAddress(document.get("emailAddress").asString().getValue());
        newPlayerAccount.setPassword(document.get("password").asString().getValue());
        newPlayerAccount.setPlayerCharacterList(playerCharacterList);

        return newPlayerAccount;
    }
}
