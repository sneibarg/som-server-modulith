package org.springy.som.modulith.domain.note.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    private final String noteIdMissing = "ROM note id must be provided";
    private final String noteMissing = "ROM note not found in repository";
    private final String noteNotProvided = "ROM note must be provided";
    private NoteRepository repo;
    private NoteService service;

    @BeforeEach
    void setUp() {
        repo = mock(NoteRepository.class);
        service = new NoteService(repo);
    }

    @Test
    void getAllNotes_returnsRepoResults() {
        List<NoteDocument> expected = List.of(mock(NoteDocument.class), mock(NoteDocument.class));
        when(repo.findAll()).thenReturn(expected);

        List<NoteDocument> actual = service.getAllNotes();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getNotesByType_returnsRepoResult() {
        List<NoteDocument> expected = List.of(mock(NoteDocument.class));
        when(repo.findNotesByType(0)).thenReturn(expected);

        List<NoteDocument> actual = service.getNotesByType(0);

        assertThat(actual).isSameAs(expected);
        verify(repo).findNotesByType(0);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getNoteById_returnsRepoResult() {
        NoteDocument expected = mock(NoteDocument.class);
        when(repo.findNoteById("N1")).thenReturn(expected);

        NoteDocument actual = service.getNoteById("N1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findNoteById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createNote_nullNote_throwsInvalidNoteException() {
        assertThatThrownBy(() -> service.createNote(null))
                .isInstanceOf(InvalidNoteException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void createNote_blankId_throwsInvalid() {
        NoteDocument n = mock(NoteDocument.class);
        when(n.getId()).thenReturn("  ");

        assertThatThrownBy(() -> service.createNote(n))
                .isInstanceOf(InvalidNoteException.class)
                .hasMessageContaining(noteIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createNote_success_savesAndReturns() {
        NoteDocument input = mock(NoteDocument.class);
        when(input.getId()).thenReturn("N1");
        when(repo.save(input)).thenReturn(input);

        NoteDocument out = service.createNote(input);

        assertThat(out).isSameAs(input);
        verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createNote_dataAccess_becomesNotePersistenceException() {
        NoteDocument n = mock(NoteDocument.class);
        when(n.getId()).thenReturn("N1");
        when(repo.save(n)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createNote(n))
                .isInstanceOf(NotePersistenceException.class)
                .hasMessageContaining("Failed to create note");

        verify(repo).save(n);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createNote_dataAccess_safeIdExceptionPath_becomesNotePersistenceException() {
        NoteDocument note = mock(NoteDocument.class);

        when(note.getId()).thenReturn("N1").thenThrow(new RuntimeException("boom"));
        when(repo.save(note)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createNote(note))
                .isInstanceOf(NotePersistenceException.class)
                .hasMessageContaining("Failed to create note");

        verify(repo).save(note);
        verifyNoMoreInteractions(repo);
    }


    @Test
    void saveNoteForId_blankId_throwsNullPointerException() {
        NoteDocument n = mock(NoteDocument.class);
        when(repo.findNoteById("  ")).thenReturn(null);

        assertThatThrownBy(() -> service.saveNoteForId("  ", n))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findNoteById("  ");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveNoteForId_nullNote_throwsNullPointerException() {
        when(repo.findNoteById("N1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveNoteForId("N1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findNoteById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveNoteForId_blankNoteId_throwsNullPointerException() {
        NoteDocument input = mock(NoteDocument.class);
        when(repo.findNoteById("N1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveNoteForId("N1", input))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findNoteById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveNoteForId_success_savesLookupResult() {
        NoteDocument payload = mock(NoteDocument.class);
        NoteDocument existing = mock(NoteDocument.class);

        when(existing.getId()).thenReturn("N1");
        when(repo.findNoteById("N1")).thenReturn(existing);
        when(repo.save(payload)).thenReturn(payload);

        NoteDocument out = service.saveNoteForId("N1", payload);

        assertThat(out).isSameAs(payload);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findNoteById("N1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(payload);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteNoteById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deleteNoteById(" "))
                .isInstanceOf(InvalidNoteException.class)
                .hasMessageContaining(noteIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteNoteById_notFound_propagatesNoteNotFound() {
        when(repo.existsById("N1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteNoteById("N1"))
                .isInstanceOf(NoteNotFoundException.class);

        verify(repo).existsById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteNoteById_success_deletes() {
        when(repo.existsById("N1")).thenReturn(true);

        service.deleteNoteById("N1");

        verify(repo).existsById("N1");
        verify(repo).deleteById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteNoteById_dataAccess_becomesNotePersistenceException() {
        when(repo.existsById("N1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("N1");

        assertThatThrownBy(() -> service.deleteNoteById("N1"))
                .isInstanceOf(NotePersistenceException.class)
                .hasMessageContaining("Failed to delete note: N1");

        verify(repo).existsById("N1");
        verify(repo).deleteById("N1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllNotes_success_returnsCount() {
        when(repo.count()).thenReturn(7L);

        long out = service.deleteAllNotes();

        assertThat(out).isEqualTo(7L);
        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllNotes_dataAccess_becomesNotePersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllNotes())
                .isInstanceOf(NotePersistenceException.class)
                .hasMessageContaining("Failed to delete all notes");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllNotesFallback_returnsEmptyList() throws Exception {
        Method m = NoteService.class.getDeclaredMethod("getAllNotesFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<NoteDocument> out = (List<NoteDocument>) m.invoke(service, new RuntimeException("boom"));

        assertThat(out).isNotNull().isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getNoteByIdFallback_throwsNotePersistenceException() throws Exception {
        Method m = NoteService.class.getDeclaredMethod("getNoteByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        Throwable t = new RuntimeException("boom");
        Throwable thrown = catchThrowable(() -> {
            try {
                m.invoke(service, "N1", t);
            } catch (Exception e) {
                throw e.getCause() != null ? (RuntimeException) e.getCause() : e;
            }
        });

        assertThat(thrown)
                .isInstanceOf(NotePersistenceException.class)
                .hasMessageContaining("NoteDocument lookup temporarily unavailable: N1");

        verifyNoInteractions(repo);
    }
}
