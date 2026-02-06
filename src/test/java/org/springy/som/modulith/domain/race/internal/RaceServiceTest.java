package org.springy.som.modulith.domain.race.internal;

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
class RaceServiceTest {
    private final String raceIdMissing = "ROM race id must be provided";
    private final String raceMissing = "ROM race must be provided";

    @Mock
    private RaceRepository repo;
    private RaceService service;

    @BeforeEach
    void setUp() {
        service = new RaceService(repo);
    }

    @Test
    void getAllRomRaces_ok() {
        List<RaceDocument> races = List.of(mock(RaceDocument.class), mock(RaceDocument.class));
        when(repo.findAll()).thenReturn(races);

        assertThat(service.getAllRaces()).isSameAs(races);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRomRaceByName_delegates() {
        RaceDocument race = mock(RaceDocument.class);
        when(repo.findRomRaceById("human")).thenReturn(race);

        assertThat(service.getRaceByName("human")).isSameAs(race);

        verify(repo).findRomRaceById("human");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRomRaceById_delegates() {
        RaceDocument race = mock(RaceDocument.class);
        when(repo.findRomRaceById("R1")).thenReturn(race);

        assertThat(service.getRaceById("R1")).isSameAs(race);

        verify(repo).findRomRaceById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_null_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.createRace(null))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRomRace_blankId_becomesInvalidRomRaceException() {
        RaceDocument race = mock(RaceDocument.class);
        when(race.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createRace(race))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRomRace_ok_saves() {
        RaceDocument race = mock(RaceDocument.class);
        when(race.getId()).thenReturn("R1");
        when(repo.save(race)).thenReturn(race);

        assertThat(service.createRace(race)).isSameAs(race);

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_dataAccess_becomesRomRacePersistenceException() {
        RaceDocument race = mock(RaceDocument.class);
        when(race.getId()).thenReturn("R1");
        when(repo.save(race)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRace(race))
                .isInstanceOf(RacePersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_dataAccess_safeIdExceptionPath_becomesRomRacePersistenceException() {
        RaceDocument race = mock(RaceDocument.class);
        when(race.getId()).thenReturn("R1").thenThrow(new RuntimeException("boom"));
        when(repo.save(race)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRace(race))
                .isInstanceOf(RacePersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveRomRaceForId_blankId_becomesInvalidRomRaceException() {
        RaceDocument race = mock(RaceDocument.class);

        assertThatThrownBy(() -> service.saveRaceForId(" ", race))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomRaceForId_nullRace_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.saveRaceForId("R1", null))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomRaceForId_ok_savesByLookup() {
        RaceDocument input = mock(RaceDocument.class);
        when(input.getId()).thenReturn("R1");

        RaceDocument existing = mock(RaceDocument.class);
        when(repo.findRomRaceById("R1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveRaceForId("R1", input)).isSameAs(existing);

        verify(repo).findRomRaceById("R1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_blankId_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.deleteRaceById(""))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteRomRaceById_notFound_becomesRomRaceNotFoundException() {
        when(repo.existsById("R1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteRaceById("R1"))
                .isInstanceOf(RaceNotFoundException.class);

        verify(repo).existsById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_ok_deletes() {
        when(repo.existsById("R1")).thenReturn(true);

        service.deleteRaceById("R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_dataAccess_becomesMobilePersistenceException() {
        when(repo.existsById("R1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("R1");

        assertThatThrownBy(() -> service.deleteRaceById("R1"))
                .isInstanceOf(RacePersistenceException.class)
                .hasMessageContaining("Failed to delete ROM race: R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomRace_ok_returnsCount() {
        when(repo.count()).thenReturn(4L);

        assertThat(service.deleteAllRaces()).isEqualTo(4L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomRace_dataAccess_becomesRomRacePersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllRaces())
                .isInstanceOf(RacePersistenceException.class)
                .hasMessageContaining("Failed to delete all ROM race");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllRomRacesFallback_returnsEmptyList() throws Exception {
        var m = RaceService.class.getDeclaredMethod("getAllRomRacesFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<RaceDocument> out = (List<RaceDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getRomRaceByIdFallback_throwsMobilePersistenceException() throws Exception {
        var m = RaceService.class.getDeclaredMethod("getRomRaceByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "R1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RacePersistenceException.class)
                .hasMessageContaining("temporarily unavailable: R1");

        verifyNoInteractions(repo);
    }
}
