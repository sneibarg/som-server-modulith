package org.springy.som.modulith.domain.character.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.domain.ServiceGuards;
import org.springy.som.modulith.domain.character.api.CharacterDeletedEvent;
import org.springy.som.modulith.domain.character.api.NewCharacterEvent;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CharacterServiceTest {

    private CharacterRepository repo;
    private ApplicationEventPublisher eventPublisher;
    private CharacterService service;

    @BeforeEach
    void setUp() {
        repo = mock(CharacterRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new CharacterService(repo, eventPublisher);
    }

    @Test
    void getAllPlayerCharacters_ok_returnsList() {
        List<CharacterDocument> expected = List.of(new CharacterDocument(), new CharacterDocument());
        when(repo.findAll()).thenReturn(expected);

        List<CharacterDocument> actual = service.getAllPlayerCharacters();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void getAllPlayerCharacters_dataAccess_becomesPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getAllPlayerCharacters())
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to load player characters");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void getPlayerCharactersByAccountId_ok_callsRepo() {
        List<CharacterDocument> expected = List.of(new CharacterDocument());
        when(repo.findAllByAccountId("acct1")).thenReturn(expected);

        List<CharacterDocument> actual = service.getPlayerCharactersByAccountId("acct1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findAllByAccountId("acct1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void getPlayerCharactersByAccountId_blankAccountId_becomesInvalidPlayerCharacterException() {
        assertThatThrownBy(() -> service.getPlayerCharactersByAccountId(""))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("CharacterDocument id must be provided");

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
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void getPlayerCharacterById_ok_returnsEntity() {
        CharacterDocument pc = new CharacterDocument();
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(pc);

        CharacterDocument actual = service.getPlayerCharacterById("C1");

        assertThat(actual).isSameAs(pc);
        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void getPlayerCharacterById_dataAccess_becomesPersistenceException() {
        when(repo.findPlayerCharacterByCharacterId("C1"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getPlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to load player character");

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    @Disabled
    void savePlayerCharacter_null_throwsInvalid() {
        assertThatThrownBy(() -> service.createPlayerCharacter(null))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    @Disabled
    void savePlayerCharacter_blankId_throwsInvalid() {
        CharacterDocument pc = mock(CharacterDocument.class);
        when(pc.getId()).thenReturn(" ");

        assertThatThrownBy(() -> service.createPlayerCharacter(pc))
                .isInstanceOf(InvalidPlayerCharacterException.class)
                .hasMessageContaining("id must be provided");

        verify(pc).getId();
        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerCharacter_ok_saves() {
        CharacterDocument pc = mock(CharacterDocument.class);
//        when(pc.getId()).thenReturn("C1");
        when(pc.getName()).thenReturn("TestChar");
        when(pc.getAccountId()).thenReturn("A1");
        when(pc.getId()).thenReturn("C1");
        when(repo.findByName("TestChar")).thenReturn(null);
        when(repo.save(pc)).thenReturn(pc);

        CharacterDocument actual = service.createPlayerCharacter(pc);

        assertThat(actual).isSameAs(pc);

        InOrder inOrder = inOrder(pc, repo, eventPublisher);
//        inOrder.verify(pc).getId();
        inOrder.verify(pc).getName();
        inOrder.verify(repo).findByName("TestChar");
        inOrder.verify(repo).save(pc);
        inOrder.verify(eventPublisher).publishEvent(any(NewCharacterEvent.class));
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void savePlayerCharacter_duplicateName_throwsDuplicateCharacterNameException() {
        CharacterDocument pc = mock(CharacterDocument.class);
        CharacterDocument existing = mock(CharacterDocument.class);
        when(pc.getName()).thenReturn("DuplicateName");
        when(repo.findByName("DuplicateName")).thenReturn(existing);

        assertThatThrownBy(() -> service.createPlayerCharacter(pc))
                .isInstanceOf(DuplicateCharacterNameException.class)
                .hasMessageContaining("DuplicateName");

        verify(pc).getName();
        verify(repo).findByName("DuplicateName");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void savePlayerCharacterForId_blankId_throwsNullPointerException() {
        CharacterDocument pc = mock(CharacterDocument.class);
        when(repo.findPlayerCharacterByCharacterId(" ")).thenReturn(null);

        assertThatThrownBy(() -> service.savePlayerCharacterForId(" ", pc))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findPlayerCharacterByCharacterId(" ");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void savePlayerCharacterForId_nullBody_throwsNullPointerException() {
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(null);

        assertThatThrownBy(() -> service.savePlayerCharacterForId("C1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void savePlayerCharacterForId_ok_loadsThenSavesLoadedEntity() {
        CharacterDocument input = mock(CharacterDocument.class);
        CharacterDocument existing = mock(CharacterDocument.class);

        when(existing.getId()).thenReturn("C1");
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(existing);
        when(repo.save(input)).thenReturn(input);

        CharacterDocument actual = service.savePlayerCharacterForId("C1", input);

        assertThat(actual).isSameAs(input);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findPlayerCharacterByCharacterId("C1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(input);

        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deletePlayerCharacterById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deletePlayerCharacterById(""))
                .isInstanceOf(InvalidPlayerCharacterException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void deletePlayerCharacterById_notFound_throwsNotFound() {
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(null);

        assertThatThrownBy(() -> service.deletePlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterNotFoundException.class)
                .hasMessageContaining("C1");

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deletePlayerCharacterById_ok_deletes() {
        CharacterDocument existing = mock(CharacterDocument.class);
        when(existing.getAccountId()).thenReturn("A1");
        when(repo.findPlayerCharacterByCharacterId("C1")).thenReturn(existing);

        service.deletePlayerCharacterById("C1");

        InOrder inOrder = inOrder(repo, eventPublisher);
        inOrder.verify(repo).findPlayerCharacterByCharacterId("C1");
        inOrder.verify(repo).deleteById("C1");
        inOrder.verify(eventPublisher).publishEvent(any(CharacterDeletedEvent.class));
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deletePlayerCharacterById_dataAccess_becomesPersistenceException() {
        when(repo.findPlayerCharacterByCharacterId("C1"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deletePlayerCharacterById("C1"))
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to delete player character");

        verify(repo).findPlayerCharacterByCharacterId("C1");
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deleteAllPlayerCharacters_ok_returnsCountAndDeletes() {
        when(repo.count()).thenReturn(7L);

        long deleted = service.deleteAllPlayerCharacters();

        assertThat(deleted).isEqualTo(7L);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).count();
        inOrder.verify(repo).deleteAll();
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deleteAllPlayerCharacters_dataAccess_becomesPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllPlayerCharacters())
                .isInstanceOf(PlayerCharacterPersistenceException.class)
                .hasMessageContaining("Failed to delete all player characters");

        verify(repo).count();
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void safeId_whenGetIdThrows_returnsNull_reflection() {
        CharacterDocument badPc = mock(CharacterDocument.class);
        when(badPc.getId()).thenThrow(new RuntimeException("boom"));

        String result = ServiceGuards.safeId(badPc, CharacterDocument::getId);

        assertThat(result).isNull();
    }

    @Test
    void getAllPlayerCharactersFallback_returnsEmptyList_reflection() throws Exception {
        Method m = CharacterService.class.getDeclaredMethod("getAllPlayerCharactersFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<CharacterDocument> result = (List<CharacterDocument>) m.invoke(service, new RuntimeException("cb"));

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
