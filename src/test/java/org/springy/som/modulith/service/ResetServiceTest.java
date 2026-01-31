package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.reset.Reset;
import org.springy.som.modulith.exception.race.RomRacePersistenceException;
import org.springy.som.modulith.exception.reset.InvalidResetException;
import org.springy.som.modulith.exception.reset.ResetNotFoundException;
import org.springy.som.modulith.exception.reset.ResetPersistenceException;
import org.springy.som.modulith.repository.ResetRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetServiceTest {
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
        List<Reset> resets = List.of(mock(Reset.class), mock(Reset.class));
        when(repo.findAll()).thenReturn(resets);

        assertThat(service.getAllResets()).isSameAs(resets);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getResetByName_delegates() {
        Reset reset = mock(Reset.class);
        when(repo.findResetById("foo")).thenReturn(reset);

        assertThat(service.getResetByName("foo")).isSameAs(reset);

        verify(repo).findResetById("foo");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getResetById_delegates() {
        Reset reset = mock(Reset.class);
        when(repo.findResetById("R1")).thenReturn(reset);

        assertThat(service.getResetById("R1")).isSameAs(reset);

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
        Reset reset = mock(Reset.class);
        when(reset.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createReset(reset))
                .isInstanceOf(InvalidResetException.class)
                .hasMessageContaining(resetIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createReset_ok_saves() {
        Reset reset = mock(Reset.class);
        when(reset.getId()).thenReturn("RS1");
        when(repo.save(reset)).thenReturn(reset);

        assertThat(service.createReset(reset)).isSameAs(reset);

        verify(repo).save(reset);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createReset_dataAccess_becomesRomRacePersistenceException() {
        Reset reset = mock(Reset.class);
        when(reset.getId()).thenReturn("RS1");
        when(repo.save(reset)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createReset(reset))
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(reset);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createReset_dataAccess_safeIdExceptionPath_becomesRomRacePersistenceException() {
        Reset reset = mock(Reset.class);
        when(reset.getId()).thenReturn("RS1").thenThrow(new RuntimeException("boom"));
        when(repo.save(reset)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createReset(reset))
                .isInstanceOf(ResetPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(reset);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveResetForId_blankId_becomesInvalidResetException() {
        Reset reset = mock(Reset.class);

        assertThatThrownBy(() -> service.saveResetForId(" ", reset))
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
        Reset input = mock(Reset.class);
        when(input.getId()).thenReturn("RS1");

        Reset existing = mock(Reset.class);
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
        List<Reset> out = (List<Reset>) m.invoke(service, new RuntimeException("cb"));

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
