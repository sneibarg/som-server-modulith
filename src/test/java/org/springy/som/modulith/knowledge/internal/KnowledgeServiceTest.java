package org.springy.som.modulith.knowledge.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {
    @Mock
    private KnowledgeRepository knowledgeRepository;

    private KnowledgeService knowledgeService;

    @BeforeEach
    void setUp() {
        knowledgeService = new KnowledgeService(knowledgeRepository);
    }

    @Test
    void get_missing_throwsNotFound() {
        when(knowledgeRepository.findById("hero")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> knowledgeService.get("hero"))
                .isInstanceOf(KnowledgeNotFoundException.class)
                .hasMessage("No knowledge stored for hero");
    }

    @Test
    void get_blankId_throwsInvalid() {
        assertThatThrownBy(() -> knowledgeService.get("  "))
                .isInstanceOf(InvalidKnowledgeException.class)
                .hasMessage("characterId must not be blank");
        verifyNoInteractions(knowledgeRepository);
    }

    @Test
    void get_dataAccess_hidesTheDriverMessage() {
        when(knowledgeRepository.findById("hero")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> knowledgeService.get("hero"))
                .isInstanceOf(KnowledgePersistenceException.class)
                .hasMessage("Failed to load knowledge");
    }

    @Test
    void save_mismatch_isRejected() {
        KnowledgeDocument document = new KnowledgeDocument();
        document.setCharacterId("other");

        assertThatThrownBy(() -> knowledgeService.save("hero", document))
                .isInstanceOf(InvalidKnowledgeException.class)
                .hasMessage("characterId does not match the path");
        verifyNoInteractions(knowledgeRepository);
    }

    @Test
    void save_caseDifference_isRejected() {
        KnowledgeDocument document = new KnowledgeDocument();
        document.setCharacterId("Hero");

        assertThatThrownBy(() -> knowledgeService.save("hero", document))
                .isInstanceOf(InvalidKnowledgeException.class);
        verifyNoInteractions(knowledgeRepository);
    }

    @Test
    void save_setsPathId_preservesOrder_andFillsNullLists() {
        KnowledgeDocument.Being goblin = being("goblin", null);
        KnowledgeDocument.Being wolf = being("wolf", List.of("snarling"));
        KnowledgeDocument.HelpTopic help = new KnowledgeDocument.HelpTopic();
        help.setCommand("look");
        help.setTopics(null);
        KnowledgeDocument document = new KnowledgeDocument();
        document.setCharacterId("  ");
        document.setLoginLines(null);
        document.setHelpTopics(new ArrayList<>(List.of(help)));
        document.setCommands(null);
        document.setAreas(null);
        document.setMobiles(new ArrayList<>(List.of(goblin, wolf)));
        document.setNpcs(null);
        document.setPlayers(null);
        document.setRoom(null);
        when(knowledgeRepository.save(document)).thenReturn(document);

        KnowledgeDocument saved = knowledgeService.save("hero", document);

        assertThat(saved.getCharacterId()).isEqualTo("hero");
        assertThat(saved.getLoginLines()).isEmpty();
        assertThat(saved.getHelpTopics()).containsExactly(help);
        assertThat(saved.getCommands()).isEmpty();
        assertThat(help.getTopics()).isEmpty();
        assertThat(saved.getMobiles()).containsExactly(goblin, wolf);
        assertThat(goblin.getAuras()).isEmpty();
        assertThat(wolf.getAuras()).containsExactly("snarling");
        assertThat(saved.getNpcs()).isEmpty();
        assertThat(saved.getPlayers()).isEmpty();
        assertThat(saved.getAreas()).isEmpty();
        assertThat(saved.getRoom()).isNull();
        verify(knowledgeRepository).save(document);
    }

    @Test
    void save_keepsRoomAndNestedOrder() {
        KnowledgeDocument.Room room = new KnowledgeDocument.Room();
        room.setName("Temple");
        room.setExits(new ArrayList<>(List.of("north", "east")));
        room.setItemLines(null);
        KnowledgeDocument.Player player = new KnowledgeDocument.Player();
        player.setName("Ada");
        player.setAuras(new ArrayList<>(List.of("glowing", "humming")));
        KnowledgeDocument document = new KnowledgeDocument();
        document.setPlayers(new ArrayList<>(List.of(player)));
        document.setRoom(room);
        when(knowledgeRepository.save(document)).thenReturn(document);

        KnowledgeDocument saved = knowledgeService.save("hero", document);

        assertThat(saved.getPlayers().get(0).getAuras()).containsExactly("glowing", "humming");
        assertThat(saved.getRoom().getExits()).containsExactly("north", "east");
        assertThat(saved.getRoom().getItemLines()).isEmpty();
    }

    @Test
    void save_dataAccess_hidesTheDriverMessage() {
        KnowledgeDocument document = new KnowledgeDocument();
        when(knowledgeRepository.save(document)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> knowledgeService.save("hero", document))
                .isInstanceOf(KnowledgePersistenceException.class)
                .hasMessage("Failed to save knowledge");
    }

    private static KnowledgeDocument.Being being(String name, List<String> auras) {
        KnowledgeDocument.Being being = new KnowledgeDocument.Being();
        being.setName(name);
        being.setAuras(auras == null ? null : new ArrayList<>(auras));
        return being;
    }
}
