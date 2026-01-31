package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.race.RomRace;
import org.springy.som.modulith.exception.mobile.MobilePersistenceException;
import org.springy.som.modulith.exception.race.InvalidRomRaceException;
import org.springy.som.modulith.exception.race.RomRaceNotFoundException;
import org.springy.som.modulith.exception.race.RomRacePersistenceException;
import org.springy.som.modulith.repository.RaceRepository;
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
        List<RomRace> races = List.of(mock(RomRace.class), mock(RomRace.class));
        when(repo.findAll()).thenReturn(races);

        assertThat(service.getAllRomRaces()).isSameAs(races);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRomRaceByName_delegates() {
        RomRace race = mock(RomRace.class);
        when(repo.findRomRaceById("human")).thenReturn(race);

        assertThat(service.getRomRaceByName("human")).isSameAs(race);

        verify(repo).findRomRaceById("human");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRomRaceById_delegates() {
        RomRace race = mock(RomRace.class);
        when(repo.findRomRaceById("R1")).thenReturn(race);

        assertThat(service.getRomRaceById("R1")).isSameAs(race);

        verify(repo).findRomRaceById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_null_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.createRomRace(null))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRomRace_blankId_becomesInvalidRomRaceException() {
        RomRace race = mock(RomRace.class);
        when(race.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createRomRace(race))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRomRace_ok_saves() {
        RomRace race = mock(RomRace.class);
        when(race.getId()).thenReturn("R1");
        when(repo.save(race)).thenReturn(race);

        assertThat(service.createRomRace(race)).isSameAs(race);

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_dataAccess_becomesRomRacePersistenceException() {
        RomRace race = mock(RomRace.class);
        when(race.getId()).thenReturn("R1");
        when(repo.save(race)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRomRace(race))
                .isInstanceOf(RomRacePersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomRace_dataAccess_safeIdExceptionPath_becomesRomRacePersistenceException() {
        RomRace race = mock(RomRace.class);
        when(race.getId()).thenReturn("R1").thenThrow(new RuntimeException("boom"));
        when(repo.save(race)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRomRace(race))
                .isInstanceOf(RomRacePersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(race);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveRomRaceForId_blankId_becomesInvalidRomRaceException() {
        RomRace race = mock(RomRace.class);

        assertThatThrownBy(() -> service.saveRomRaceForId(" ", race))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomRaceForId_nullRace_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.saveRomRaceForId("R1", null))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomRaceForId_ok_savesByLookup() {
        RomRace input = mock(RomRace.class);
        when(input.getId()).thenReturn("R1");

        RomRace existing = mock(RomRace.class);
        when(repo.findRomRaceById("R1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveRomRaceForId("R1", input)).isSameAs(existing);

        verify(repo).findRomRaceById("R1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_blankId_becomesInvalidRomRaceException() {
        assertThatThrownBy(() -> service.deleteRomRaceById(""))
                .isInstanceOf(InvalidRomRaceException.class)
                .hasMessageContaining(raceIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteRomRaceById_notFound_becomesRomRaceNotFoundException() {
        when(repo.existsById("R1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteRomRaceById("R1"))
                .isInstanceOf(RomRaceNotFoundException.class);

        verify(repo).existsById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_ok_deletes() {
        when(repo.existsById("R1")).thenReturn(true);

        service.deleteRomRaceById("R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomRaceById_dataAccess_becomesMobilePersistenceException() {
        when(repo.existsById("R1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("R1");

        assertThatThrownBy(() -> service.deleteRomRaceById("R1"))
                .isInstanceOf(RomRacePersistenceException.class)
                .hasMessageContaining("Failed to delete ROM race: R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomRace_ok_returnsCount() {
        when(repo.count()).thenReturn(4L);

        assertThat(service.deleteAllRomRace()).isEqualTo(4L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomRace_dataAccess_becomesRomRacePersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllRomRace())
                .isInstanceOf(RomRacePersistenceException.class)
                .hasMessageContaining("Failed to delete all ROM race");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllRomRacesFallback_returnsEmptyList() throws Exception {
        var m = RaceService.class.getDeclaredMethod("getAllRomRacesFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<RomRace> out = (List<RomRace>) m.invoke(service, new RuntimeException("cb"));

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
        }).isInstanceOf(RomRacePersistenceException.class)
                .hasMessageContaining("temporarily unavailable: R1");

        verifyNoInteractions(repo);
    }
}
