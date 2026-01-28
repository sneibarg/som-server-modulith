package org.springy.som.modulith.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.area.Area;
import org.springy.som.modulith.repository.AreaRepository;
import org.springy.som.modulith.exception.area.AreaNotFoundException;
import org.springy.som.modulith.exception.area.AreaPersistenceException;
import org.springy.som.modulith.exception.area.InvalidAreaException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceTest {

    @Mock AreaRepository areaRepository;

    @InjectMocks AreaService areaService;

    @Test
    void getAllAreas_returnsRepositoryResults() {
        List<Area> expected = List.of(mock(Area.class), mock(Area.class));
        when(areaRepository.findAll()).thenReturn(expected);

        List<Area> actual = areaService.getAllAreas();

        assertThat(actual).isSameAs(expected);
        verify(areaRepository).findAll();
    }

    @Test
    void getAllAreas_wrapsDataAccessException() {
        when(areaRepository.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.getAllAreas())
                .isInstanceOf(AreaPersistenceException.class);

        verify(areaRepository).findAll();
    }

    @Test
    void getAreaById_blankId_throwsInvalidAreaException() {
        assertThatThrownBy(() -> areaService.getAreaById("  "))
                .isInstanceOf(InvalidAreaException.class);

        verifyNoInteractions(areaRepository);
    }

    @Test
    void getAreaById_missing_throwsNotFound() {
        when(areaRepository.findAreaByAreaId("A1")).thenReturn(null);

        assertThatThrownBy(() -> areaService.getAreaById("A1"))
                .isInstanceOf(AreaNotFoundException.class);

        verify(areaRepository).findAreaByAreaId("A1");
    }

    @Test
    void getAreaById_wrapsDataAccessException() {
        when(areaRepository.findAreaByAreaId("A1"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> areaService.getAreaById("A1"))
                .isInstanceOf(AreaPersistenceException.class);

        verify(areaRepository).findAreaByAreaId("A1");
    }

    @Test
    void createArea_saves() {
        Area a = mock(Area.class);
        when(areaRepository.save(a)).thenReturn(a);
        when(a.getId()).thenReturn("abcd-1234-dcba-4321");
        Area created = areaService.createArea(a);

        assertThat(created).isSameAs(a);
        verify(areaRepository).save(a);
    }

    @Test
    void deleteAllAreas_returnsCountAndDeletesAll() {
        when(areaRepository.count()).thenReturn(5L);

        long count = areaService.deleteAllAreas();

        assertThat(count).isEqualTo(5L);
        verify(areaRepository).count();
        verify(areaRepository).deleteAll();
    }
}
