package org.springy.som.modulith.util;

import org.bson.BsonDocument;
import org.bson.json.JsonObject;
import org.springy.som.modulith.domain.character.PlayerCharacter;

import java.util.ArrayList;
import java.util.List;

public class PlayerCharacterUtil {
    public static PlayerCharacter getPCFromJson(String playerJson) {
        PlayerCharacter playerCharacter = new PlayerCharacter();
        JsonObject json = new JsonObject(playerJson);
        BsonDocument document = json.toBsonDocument();
        List<String> playerCharacterList = new ArrayList<>();

        playerCharacter.setId(String.valueOf(document.getString("id")));

        return playerCharacter;
    }

    private void addPlayerCharacters(List<String> playerCharacterList) {

    }
}
