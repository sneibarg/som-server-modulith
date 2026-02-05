package org.springy.som.modulith.domain.reset.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetDocumentServiceTest {
    private final String resetIdMissing = "ROM reset id must be provided";
    private final String resetMissing = "ROM reset must be provided";
    private final String dbDown = "Service unavailable Failed to delete all ROM resets org.springframework.dao.DataAccessResourceFailureException: db down";

    @Mock
    private ResetRepository repo;
    private ResetService service;

    @BeforeEach
    void setUp() {
        service = new ResetService(repo);
    }

    @Test
    void getAllResets_ok() {
        List<ResetDocument> resetDocuments = List.of(mock(ResetDocument.class), mock(ResetDocument.class));
        when(repo.findAll()).thenReturn(resetDocuments);

        assertThat(service.getAllResets()).isSameAs(resetDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getResetByName_delegates() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(repo.findResetById("foo")).thenReturn(resetDocument);

        assertThat(service.getResetByName("foo")).isSameAs(resetDocument);

        verify(repo).findResetById("foo");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getResetById_delegates() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(repo.findResetById("R1")).thenReturn(resetDocument);

        assertThat(service.getResetById("R1")).isSameAs(resetDocument);

        verify(repo).findResetById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createReset_null_becomesInvalidResetException() {
        assertThatThrownBy(() -> service.createReset(null))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createReset_blankId_becomesInvalidResetException() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(resetDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createReset(resetDocument))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createReset_ok_saves() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(resetDocument.getId()).thenReturn("RS1");
        when(repo.save(resetDocument)).thenReturn(resetDocument);

        assertThat(service.createReset(resetDocument)).isSameAs(resetDocument);

        verify(repo).save(resetDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createReset_dataAccess_becomesRomRacePersistenceException() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(resetDocument.getId()).thenReturn("RS1");
        when(repo.save(resetDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createReset(resetDocument))
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(resetDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createReset_dataAccess_safeIdExceptionPath_becomesRomRacePersistenceException() {
        ResetDocument resetDocument = mock(ResetDocument.class);
        when(resetDocument.getId()).thenReturn("RS1").thenThrow(new RuntimeException("boom"));
        when(repo.save(resetDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createReset(resetDocument))
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(resetDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveResetForId_blankId_becomesInvalidResetException() {
        ResetDocument resetDocument = mock(ResetDocument.class);

        assertThatThrownBy(() -> service.saveResetForId(" ", resetDocument))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveResetForId_nullReset_becomesInvalidResetException() {
        assertThatThrownBy(() -> service.saveResetForId("RS1", null))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveResetForId_ok_savesByLookup() {
        ResetDocument input = mock(ResetDocument.class);
        when(input.getId()).thenReturn("RS1");

        ResetDocument existing = mock(ResetDocument.class);
        when(repo.findResetById("RS1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveResetForId("RS1", input)).isSameAs(existing);

        verify(repo).findResetById("RS1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteResetById_blankId_becomesInvalidResetException() {
        assertThatThrownBy(() -> service.deleteResetById(""))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteResetById_notFound_becomesResetNotFoundException() {
        when(repo.existsById("RS1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteResetById("RS1"))
                .isInstanceOf(ResetNotFoundException.class);

        verify(repo).existsById("RS1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteResetById_ok_deletes() {
        when(repo.existsById("RS1")).thenReturn(true);

        service.deleteResetById("RS1");

        verify(repo).existsById("RS1");
        verify(repo).deleteById("RS1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteResetById_dataAccess_becomesResetPersistenceException() {
        when(repo.existsById("RS1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("RS1");

        assertThatThrownBy(() -> service.deleteResetById("RS1"))
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("Failed to delete ROM reset: RS1");

        verify(repo).existsById("RS1");
        verify(repo).deleteById("RS1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllResets_ok_returnsCount() {
        when(repo.count()).thenReturn(3L);

        assertThat(service.deleteAllResets()).isEqualTo(3L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllResets_dataAccess_becomesRomRacePersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllResets())
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining(dbDown);

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllResetsFallback_returnsEmptyList() throws Exception {
        var m = ResetService.class.getDeclaredMethod("getAllResetsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ResetDocument> out = (List<ResetDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getResetByIdFallback_throwsResetPersistenceException() throws Exception {
        var m = ResetService.class.getDeclaredMethod("getResetByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "RS1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: RS1");

        verifyNoInteractions(repo);
    }
}
