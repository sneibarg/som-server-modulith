package org.springy.som.modulith.domain.character.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springy.som.modulith.domain.player.internal.PlayerAccountRepository;
import org.springy.som.modulith.domain.player.internal.PlayerCharacterListSyncListener;
import org.springy.som.modulith.domain.player.internal.PlayerDocument;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import({
        CharacterService.class,
        PlayerCharacterListSyncListener.class
})
class CharacterPlayerListIntegrationTest {

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.embedded.version", () -> "4.0.2");
        registry.add("spring.mongodb.embedded.port", () -> 0);
        registry.add("spring.data.mongodb.database", () -> "som-test");
    }

    @Autowired
    private CharacterService characterService;
    @Autowired
    private CharacterRepository characterRepository;
    @Autowired
    private PlayerAccountRepository playerAccountRepository;

    @BeforeEach
    void setUp() {
        characterRepository.deleteAll();
        playerAccountRepository.deleteAll();
    }

    @Test
    void createAndDeleteCharacter_updatesPlayerCharacterList() {
        PlayerDocument player = new PlayerDocument();
        player.setId("A1");
        player.setAccountName("scott");
        player.setFirstName("Scott");
        player.setLastName("Neibarger");
        player.setEmailAddress("scott@example.com");
        player.setPassword("changeit");
        player.setPlayerCharacterList(new ArrayList<>());
        playerAccountRepository.save(player);

        CharacterDocument created = new CharacterDocument();
        created.setId("C1");
        created.setAccountId("A1");
        created.setName("Dain");
        characterService.createPlayerCharacter(created);

        PlayerDocument afterCreate = playerAccountRepository.findPlayerAccountById("A1");
        assertThat(afterCreate.getPlayerCharacterList()).containsExactly("C1");

        characterService.deletePlayerCharacterById("C1");

        PlayerDocument afterDelete = playerAccountRepository.findPlayerAccountById("A1");
        assertThat(afterDelete.getPlayerCharacterList()).isEmpty();
    }
}
