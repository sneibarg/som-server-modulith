package org.springy.som.modulith.domain.special.internal;

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
class SpecialDocumentServiceTest {
    private final String specialIdMissing = "ROM special id must be provided";
    private final String specialMissing = "ROM special must be provided";

    @Mock
    private SpecialRepository repo;
    private SpecialService service;

    @BeforeEach
    void setUp() {
        service = new SpecialService(repo);
    }

    @Test
    void getAllSpecials_ok() {
        List<SpecialDocument> specialDocuments = List.of(mock(SpecialDocument.class), mock(SpecialDocument.class));
        when(repo.findAll()).thenReturn(specialDocuments);

        assertThat(service.getAllSpecials()).isSameAs(specialDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpecialByName_delegates() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(repo.findSpecialById("spec")).thenReturn(specialDocument);

        assertThat(service.getSpecialByName("spec")).isSameAs(specialDocument);

        verify(repo).findSpecialById("spec");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpecialById_delegates() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(repo.findSpecialById("S1")).thenReturn(specialDocument);

        assertThat(service.getSpecialById("S1")).isSameAs(specialDocument);

        verify(repo).findSpecialById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_null_becomesInvalidSpecialException() {
        assertThatThrownBy(() -> service.createSpecial(null))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining(specialMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSpecial_blankId_becomesInvalidSpecialException() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(specialDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createSpecial(specialDocument))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining(specialIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSpecial_ok_saves() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(specialDocument.getId()).thenReturn("S1");
        when(repo.save(specialDocument)).thenReturn(specialDocument);

        assertThat(service.createSpecial(specialDocument)).isSameAs(specialDocument);

        verify(repo).save(specialDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_dataAccess_becomesSpecialPersistenceException() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(specialDocument.getId()).thenReturn("S1");
        when(repo.save(specialDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSpecial(specialDocument))
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to create ROM specialDocument");

        verify(repo).save(specialDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpecial_dataAccess_safeIdExceptionPath_becomesSpecialPersistenceException() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);
        when(specialDocument.getId()).thenReturn("S1").thenThrow(new RuntimeException("boom"));
        when(repo.save(specialDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSpecial(specialDocument))
                .isInstanceOf(SpecialPersistenceException.class)
                .hasMessageContaining("Failed to create ROM specialDocument");

        verify(repo).save(specialDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSpecialForId_blankId_becomesInvalidSpecialException() {
        SpecialDocument specialDocument = mock(SpecialDocument.class);

        assertThatThrownBy(() -> service.saveSpecialForId(" ", specialDocument))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining(specialIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveSpecialForId_nullSpecial_becomesInvalidSpecialException() {
        assertThatThrownBy(() -> service.saveSpecialForId("S1", null))
                .isInstanceOf(InvalidSpecialException.class)
                .hasMessageContaining(specialMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveSpecialForId_ok_savesByLookup() {
        SpecialDocument input = mock(SpecialDocument.class);
        when(input.getId()).thenReturn("S1");

        SpecialDocument existing = mock(SpecialDocument.class);
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
                .hasMessageContaining(specialIdMissing);

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
    void getAllSpecialsFallback_returnsEmptyList() throws Exception {
        var m = SpecialService.class.getDeclaredMethod("getAllSpecialsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SpecialDocument> out = (List<SpecialDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getSpecialByIdFallback_throwsSpecialPersistenceException() throws Exception {
        var m = SpecialService.class.getDeclaredMethod("getSpecialByIdFallback", String.class, Throwable.class);
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
