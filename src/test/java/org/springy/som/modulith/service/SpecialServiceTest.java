package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.special.Special;
import org.springy.som.modulith.exception.special.InvalidSpecialException;
import org.springy.som.modulith.exception.special.SpecialNotFoundException;
import org.springy.som.modulith.exception.special.SpecialPersistenceException;
import org.springy.som.modulith.repository.SpecialRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecialServiceTest {

    @Mock SpecialRepository repo;

    private SpecialService service;

    @BeforeEach
    void setUp() {
        service = new SpecialService(repo);
    }

    @Test
    void getAllSpecials_ok() {
        List<Special> specials = List.of(mock(Special.class), mock(Special.class));
        when(repo.findAll()).thenReturn(specials);

        assertThat(service.getAllSpecials()).isSameAs(specials);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpecialByName_delegates() {
        Special special = mock(Special.class);
        when(repo.findSpecialById("spec")).thenReturn(special);

        assertThat(service.getSpecialByName("spec")).isSameAs(special);

        verify(repo).findSpecialById("spec");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpecialById_delegates() {
        Special special = mock(Special.class);
        when(repo.findSpecialById("S1")).thenReturn(special);

        assertThat(service.getSpecialById("S1")).isSameAs(special);

        verify(repo).findSpecialById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_null_becomesInvalidSpecialException() {
        assertThatThrownBy(() -> service.createSpecial(null))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining("ROM special must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createSpecial_blankId_becomesInvalidSpecialException() {
        Special special = mock(Special.class);
        when(special.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createSpecial(special))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining("ROM special id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createSpecial_ok_saves() {
        Special special = mock(Special.class);
        when(special.getId()).thenReturn("S1");
        when(repo.save(special)).thenReturn(special);

        assertThat(service.createSpecial(special)).isSameAs(special);

        verify(repo).save(special);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_dataAccess_becomesSpecialPersistenceException() {
        Special special = mock(Special.class);
        when(special.getId()).thenReturn("S1");
        when(repo.save(special)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSpecial(special))
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to create ROM special");

        verify(repo).save(special);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_dataAccess_safeIdExceptionPath_becomesSpecialPersistenceException() {
        Special special = mock(Special.class);
        when(special.getId()).thenReturn("S1").thenThrow(new RuntimeException("boom"));
        when(repo.save(special)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSpecial(special))
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to create ROM special");

        verify(repo).save(special);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSpecialForId_blankId_becomesInvalidSpecialException() {
        Special special = mock(Special.class);

        assertThatThrownBy(() -> service.saveSpecialForId(" ", special))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining("ROM special id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveSpecialForId_nullSpecial_becomesInvalidSpecialException() {
        assertThatThrownBy(() -> service.saveSpecialForId("S1", null))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining("ROM special must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveSpecialForId_ok_savesByLookup() {
        Special input = mock(Special.class);
        when(input.getId()).thenReturn("S1");

        Special existing = mock(Special.class);
        when(repo.findSpecialById("S1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveSpecialForId("S1", input)).isSameAs(existing);

        verify(repo).findSpecialById("S1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSpecialById_blankId_becomesInvalidSpecialException() {
        assertThatThrownBy(() -> service.deleteSpecialById(""))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining("ROM special id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void deleteSpecialById_notFound_becomesSpecialNotFoundException() {
        when(repo.existsById("S1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteSpecialById("S1"))
                .isInstanceOf(SpecialNotFoundException.class);

        verify(repo).existsById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSpecialById_ok_deletes() {
        when(repo.existsById("S1")).thenReturn(true);

        service.deleteSpecialById("S1");

        verify(repo).existsById("S1");
        verify(repo).deleteById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSpecialById_dataAccess_becomesSpecialPersistenceException() {
        when(repo.existsById("S1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("S1");

        assertThatThrownBy(() -> service.deleteSpecialById("S1"))
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to delete ROM specials: S1");

        verify(repo).existsById("S1");
        verify(repo).deleteById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSpecials_ok_returnsCount() {
        when(repo.count()).thenReturn(7L);

        assertThat(service.deleteAllSpecials()).isEqualTo(7L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSpecials_dataAccess_becomesSpecialPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllSpecials())
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to delete all ROM specials");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSpecialFallback_returnsEmptyList() throws Exception {
        var m = SpecialService.class.getDeclaredMethod("getAllSpecialFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Special> out = (List<Special>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getSpecialsByIdFallback_throwsSpecialPersistenceException() throws Exception {
        var m = SpecialService.class.getDeclaredMethod("getSpecialsByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "S1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: S1");

        verifyNoInteractions(repo);
    }
}
