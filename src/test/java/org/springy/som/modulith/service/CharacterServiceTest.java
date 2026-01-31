package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.exception.character.InvalidPlayerCharacterException;
import org.springy.som.modulith.exception.character.PlayerCharacterNotFoundException;
import org.springy.som.modulith.exception.character.PlayerCharacterPersistenceException;
import org.springy.som.modulith.repository.CharacterRepository;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.util.ServiceGuards;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    private CharacterRepository repo;
    private CharacterService service;

    @BeforeEach
    void setUp() {
        repo = mock(CharacterRepository.class);
        service = new CharacterService(repo);
    }

    @Test
    void getAllPlayerCharacters_ok_returnsList() {
        List<PlayerCharacter> expected = List.of(new PlayerCharacter(), new PlayerCharacter());
        when(repo.findAll()).thenReturn(expected);

        List<PlayerCharacter> actual = service.getAllPlayerCharacters();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllPlayerCharacters_dataAccess_becomesPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getAllPlayerCharacters())
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to load player characters");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerCharactersByAccountId_ok_callsRepo() {
        List<PlayerCharacter> expected = List.of(new PlayerCharacter());
        when(repo.findAllByAccountId("acct1")).thenReturn(expected);

        List<PlayerCharacter> actual = service.getPlayerCharactersByAccountId("acct1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findAllByAccountId("acct1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerCharactersByAccountId_blankAccountId_becomesInvalidPlayerCharacterException() {
        assertThatThrownBy(() -> service.getPlayerCharactersByAccountId(""))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("PlayerCharacter id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void getPlayerCharacterById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.getPlayerCharacterById(" "))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void getPlayerCharacterById_notFound_throwsNotFound() {
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(null);

        assertThatThrownBy(() -> service.getPlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterNotFoundException.class)
                .hasMessageContaining("C1");

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerCharacterById_ok_returnsEntity() {
        PlayerCharacter pc = new PlayerCharacter();
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(pc);

        PlayerCharacter actual = service.getPlayerCharacterById("C1");

        assertThat(actual).isSameAs(pc);
        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerCharacterById_dataAccess_becomesPersistenceException() {
        when(repo.findPlayerCharacterByCharacterId("C1"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getPlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to load player character");

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void savePlayerCharacter_null_throwsInvalid() {
        assertThatThrownBy(() -> service.savePlayerCharacter(null))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerCharacter_blankId_throwsInvalid() {
        PlayerCharacter pc = mock(PlayerCharacter.class);
        when(pc.getId()).thenReturn(" ");

        assertThatThrownBy(() -> service.savePlayerCharacter(pc))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("id must be provided");

        verify(pc).getId();
        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerCharacter_ok_saves() {
        PlayerCharacter pc = mock(PlayerCharacter.class);
        when(pc.getId()).thenReturn("C1");
        when(repo.save(pc)).thenReturn(pc);

        PlayerCharacter actual = service.savePlayerCharacter(pc);

        assertThat(actual).isSameAs(pc);

        InOrder inOrder = inOrder(pc, repo);
        inOrder.verify(pc).getId();
        inOrder.verify(repo).save(pc);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void savePlayerCharacterForId_blankId_throwsInvalid() {
        PlayerCharacter pc = mock(PlayerCharacter.class);

        assertThatThrownBy(() -> service.savePlayerCharacterForId(" ", pc))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerCharacterForId_nullBody_throwsInvalid() {
        assertThatThrownBy(() -> service.savePlayerCharacterForId("C1", null))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerCharacterForId_ok_loadsThenSavesLoadedEntity() {
        PlayerCharacter input = mock(PlayerCharacter.class);
        when(input.getId()).thenReturn("C1");

        PlayerCharacter loaded = new PlayerCharacter();
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(loaded);
        when(repo.save(loaded)).thenReturn(loaded);

        PlayerCharacter actual = service.savePlayerCharacterForId("C1", input);

        assertThat(actual).isSameAs(loaded);

        InOrder inOrder = inOrder(input, repo);
        inOrder.verify(input).getId(); // requirePlayerCharacter(input) validation
        inOrder.verify(repo).findPlayerCharacterByCharacterId("C1"); // getPlayerCharacterById
        inOrder.verify(repo).save(loaded); // save loaded entity (current impl)

        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerCharacterById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deletePlayerCharacterById(""))
                .isInstanceOf(InvalidPlayerCharacterException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void deletePlayerCharacterById_notFound_throwsNotFound() {
        when(repo.existsById("C1")).thenReturn(false);

        assertThatThrownBy(() -> service.deletePlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterNotFoundException.class)
                .hasMessageContaining("C1");

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerCharacterById_ok_deletes() {
        when(repo.existsById("C1")).thenReturn(true);

        service.deletePlayerCharacterById("C1");

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).existsById("C1");
        inOrder.verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerCharacterById_dataAccess_becomesPersistenceException() {
        when(repo.existsById("C1")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deletePlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to delete player character");

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllPlayerCharacters_ok_returnsCountAndDeletes() {
        when(repo.count()).thenReturn(7L);

        long deleted = service.deleteAllPlayerCharacters();

        assertThat(deleted).isEqualTo(7L);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).count();
        inOrder.verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllPlayerCharacters_dataAccess_becomesPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllPlayerCharacters())
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to delete all player characters");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void safeId_whenGetIdThrows_returnsNull_reflection() {
        PlayerCharacter badPc = mock(PlayerCharacter.class);
        when(badPc.getId()).thenThrow(new RuntimeException("boom"));

        String result = ServiceGuards.safeId(badPc, PlayerCharacter::getId);

        assertThat(result).isNull();
    }

    @Test
    void getAllPlayerCharactersFallback_returnsEmptyList_reflection() throws Exception {
        Method m = CharacterService.class.getDeclaredMethod("getAllPlayerCharactersFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<PlayerCharacter> result = (List<PlayerCharacter>) m.invoke(service, new RuntimeException("cb"));

        assertThat(result).isEmpty();
    }

    @Test
    void getPlayerCharacterByIdFallback_throwsPersistenceException_reflection() throws Exception {
        Method m = CharacterService.class.getDeclaredMethod("getPlayerCharacterByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "C1", new RuntimeException("cb"));
            } catch (Exception e) {
                throw e.getCause();
            }
        }).isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("temporarily unavailable")
                .hasMessageContaining("C1");
    }

    @Test
    void getPlayerCharactersByAccountIdFallback_throwsPersistenceException_reflection() throws Exception {
        Method m = CharacterService.class.getDeclaredMethod("getPlayerCharactersByAccountIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "acct1", new RuntimeException("cb"));
            } catch (Exception e) {
                throw e.getCause();
            }
        }).isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("temporarily unavailable")
                .hasMessageContaining("acct1");
    }
}
