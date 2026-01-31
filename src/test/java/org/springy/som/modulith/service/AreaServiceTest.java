package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.exception.area.AreaNotFoundException;
import org.springy.som.modulith.exception.area.AreaPersistenceException;
import org.springy.som.modulith.exception.area.InvalidAreaException;
import org.springy.som.modulith.repository.AreaRepository;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springy.som.modulith.util.ServiceGuards;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {
    private final String areaIdMissing = "Area id must be provided";
    private final String areaMissing = "Area must be provided";

    @Mock
    private AreaRepository areaRepository;

    private AreaService areaService;

    @BeforeEach
    void setUp() {
        areaService = new AreaService(areaRepository);
    }

    // -------------------------
    // getAllAreas
    // -------------------------

    @Test
    void getAllAreas_ok_returnsList() {
        List<Area> expected = List.of(area("A1", "Midgaard"), area("A2", "Ofcol"));
        when(areaRepository.findAll()).thenReturn(expected);

        List<Area> result = areaService.getAllAreas();

        assertThat(result).isSameAs(expected);
        verify(areaRepository).findAll();
        verifyNoMoreInteractions(areaRepository);
    }

    @Test
    void getAllAreas_dataAccess_becomesAreaPersistenceException() {
        when(areaRepository.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.getAllAreas())
                .isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Failed to load areas");

        verify(areaRepository).findAll();
        verifyNoMoreInteractions(areaRepository);
    }

    // -------------------------
    // getAreaById
    // -------------------------

    @Test
    void getAreaById_blankId_becomesInvalidAreaException() {
        assertThatThrownBy(() -> areaService.getAreaById(" "))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaIdMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void getAreaById_missing_becomesAreaNotFoundException() {
        when(areaRepository.findAreaByAreaId("A1")).thenReturn(null);

        assertThatThrownBy(() -> areaService.getAreaById("A1"))
                .isInstanceOf(AreaNotFoundException.class);

        verify(areaRepository).findAreaByAreaId("A1");
        verifyNoMoreInteractions(areaRepository);
    }

    @Test
    void getAreaById_dataAccess_becomesAreaPersistenceException() {
        when(areaRepository.findAreaByAreaId("A1"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.getAreaById("A1"))
                .isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Failed to load area: A1");

        verify(areaRepository).findAreaByAreaId("A1");
        verifyNoMoreInteractions(areaRepository);
    }

    // -------------------------
    // createArea
    // -------------------------

    @Test
    void createArea_null_becomesInvalidAreaException() {
        assertThatThrownBy(() -> areaService.createArea(null))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void createArea_areaWithBlankId_becomesInvalidAreaException() {
        Area a = new Area();
        a.setId(""); // requireId() should fail

        assertThatThrownBy(() -> areaService.createArea(a))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaIdMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void createArea_dataAccess_becomesAreaPersistenceException_safeIdNormalPath() {
        Area input = area("A1", "Midgaard");
        when(areaRepository.save(input)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.createArea(input))
                .isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Failed to create area");

        verify(areaRepository).save(input);
        verifyNoMoreInteractions(areaRepository);
    }

    @Test
    void safeId_whenGetIdThrows_returnsNull() {
        Area badArea = mock(Area.class);
        when(badArea.getId()).thenThrow(new RuntimeException("boom"));

        String result = ServiceGuards.safeId(badArea, Area::getId);

        assertThat(result).isNull();
    }

    @Test
    void saveAreaForId_blankId_becomesInvalidAreaException() {
        Area input = area("A1", "Midgaard");

        assertThatThrownBy(() -> areaService.saveAreaForId(" ", input))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaIdMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void saveAreaForId_nullArea_becomesInvalidAreaException() {
        assertThatThrownBy(() -> areaService.saveAreaForId("A1", null))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void saveAreaForId_ok_savesFetchedArea_notProvidedArea() {
        // NOTE: current service implementation ignores the provided `area`
        // and saves getAreaById(id). This test locks in that behavior.
        Area provided = area("A1", "Provided Name");
        Area existing = area("A1", "Existing Name");

        when(areaRepository.findAreaByAreaId("A1")).thenReturn(existing);
        when(areaRepository.save(existing)).thenReturn(existing);

        Area result = areaService.saveAreaForId("A1", provided);

        assertThat(result).isSameAs(existing);

        verify(areaRepository).findAreaByAreaId("A1");
        verify(areaRepository).save(existing);
        verifyNoMoreInteractions(areaRepository);
    }

    // -------------------------
    // deleteAreaById
    // -------------------------

    @Test
    void deleteAreaById_blankId_becomesInvalidAreaException() {
        assertThatThrownBy(() -> areaService.deleteAreaById(""))
                .isInstanceOf(InvalidAreaException.class)
                .hasMessageContaining(areaIdMissing);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void deleteAreaById_missing_becomesAreaNotFoundException() {
        when(areaRepository.existsById("A1")).thenReturn(false);

        assertThatThrownBy(() -> areaService.deleteAreaById("A1"))
                .isInstanceOf(AreaNotFoundException.class);

        verify(areaRepository).existsById("A1");
        verifyNoMoreInteractions(areaRepository);
    }

    @Test
    void deleteAreaById_dataAccess_becomesAreaPersistenceException() {
        when(areaRepository.existsById("A1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down"))
                .when(areaRepository).deleteById("A1");

        assertThatThrownBy(() -> areaService.deleteAreaById("A1"))
                .isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Failed to delete area: A1");

        verify(areaRepository).existsById("A1");
        verify(areaRepository).deleteById("A1");
        verifyNoMoreInteractions(areaRepository);
    }

    // -------------------------
    // deleteAllAreas
    // -------------------------

    @Test
    void deleteAllAreas_ok_returnsCountThenDeletesAll() {
        when(areaRepository.count()).thenReturn(42L);

        long deletedCount = areaService.deleteAllAreas();

        assertThat(deletedCount).isEqualTo(42L);
        verify(areaRepository).count();
        verify(areaRepository).deleteAll();
        verifyNoMoreInteractions(areaRepository);
    }

    @Test
    void deleteAllAreas_dataAccess_becomesAreaPersistenceException() {
        when(areaRepository.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.deleteAllAreas())
                .isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Failed to delete all areas");

        verify(areaRepository).count();
        verifyNoMoreInteractions(areaRepository);
    }

    // -------------------------
    // Fallbacks (private) - invoke via reflection for 100% coverage
    // -------------------------

    @Test
    void getAllAreasFallback_returnsEmptyList() {
        @SuppressWarnings("unchecked")
        List<Area> result = (List<Area>) ReflectionTestUtils.invokeMethod(
                areaService,
                "getAllAreasFallback",
                new RuntimeException("cb open")
        );

        assertThat(result).isEmpty();
    }

    @Test
    void getAreaByIdFallback_throwsAreaPersistenceException() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                areaService,
                "getAreaByIdFallback",
                "A1",
                new RuntimeException("cb open")
        )).isInstanceOf(AreaPersistenceException.class)
                .hasMessageContaining("Area lookup temporarily unavailable: A1");
    }

    // -------------------------
    // helpers
    // -------------------------

    private static Area area(String id, String name) {
        Area a = new Area();
        a.setId(id);
        a.setName(name);
        return a;
    }
}
