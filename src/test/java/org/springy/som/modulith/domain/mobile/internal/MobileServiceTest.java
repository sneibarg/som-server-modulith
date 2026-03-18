package org.springy.som.modulith.domain.mobile.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.domain.command.internal.CommandDocument;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MobileServiceTest {
    private final String mobileIdMissing = "ROM mobile id must be provided";
    private final String mobileMissing = "ROM mobile must be provided";

    @Mock
    private MobileRepository repo;
    private MobileService service;

    @BeforeEach
    void setUp() {
        service = new MobileService(repo);
    }

    @Test
    void getAllMobiles_ok() {
        List<MobileDocument> mobileDocuments = List.of(mock(MobileDocument.class), mock(MobileDocument.class));
        when(repo.findAll()).thenReturn(mobileDocuments);

        assertThat(service.getAllMobiles()).isSameAs(mobileDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getMobileByName_delegates() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(repo.findMobileByName("orc")).thenReturn(mobileDocument);

        assertThat(service.getMobileByName("orc")).isSameAs(mobileDocument);

        verify(repo).findMobileByName("orc");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getMobileById_delegates() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(repo.findMobileById("M1")).thenReturn(mobileDocument);

        assertThat(service.getMobileById("M1")).isSameAs(mobileDocument);

        verify(repo).findMobileById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createMobile_null_becomesInvalidMobileException() {
        assertThatThrownBy(() -> service.createMobile(null))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createMobile_blankId_becomesInvalidMobileException() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(mobileDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createMobile(mobileDocument))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createMobile_ok_saves() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(mobileDocument.getId()).thenReturn("M1");
        when(repo.save(mobileDocument)).thenReturn(mobileDocument);

        assertThat(service.createMobile(mobileDocument)).isSameAs(mobileDocument);

        verify(repo).save(mobileDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createMobile_dataAccess_becomesMobilePersistenceException() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(mobileDocument.getId()).thenReturn("M1");
        when(repo.save(mobileDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createMobile(mobileDocument))
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to create mobile");

        verify(repo).save(mobileDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createMobile_dataAccess_safeIdExceptionPath_becomesMobilePersistenceException() {
        MobileDocument mobileDocument = mock(MobileDocument.class);
        when(mobileDocument.getId()).thenReturn("M1").thenThrow(new RuntimeException("boom"));
        when(repo.save(mobileDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createMobile(mobileDocument))
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to create mobile");

        verify(repo).save(mobileDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveMobileForId_blankId_becomesNullPointerException() {
        MobileDocument input = mock(MobileDocument.class);
        when(repo.findMobileById("M1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveMobileForId("M1", input))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findMobileById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveMobileForId_nullMobile_becomesInvalidMobileException() {
        when(repo.findMobileById("M1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveMobileForId("M1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findMobileById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveMobileForId_ok_savesByLookup() {
        MobileDocument input = mock(MobileDocument.class);
        MobileDocument existing = mock(MobileDocument.class);

        when(existing.getId()).thenReturn("M1");
        when(input.getId()).thenReturn("M1");
        when(repo.findMobileById("M1")).thenReturn(existing);
        when(repo.save(input)).thenReturn(input);

        assertThat(service.saveMobileForId("M1", input)).isSameAs(input);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findMobileById("M1");
        verify(existing, times(1)).getId();
        verify(input, times(1)).getId();
        inOrder.verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteMobileById_blankId_becomesInvalidMobileException() {
        assertThatThrownBy(() -> service.deleteMobileById(""))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteMobileById_notFound_becomesMobileNotFoundException() {
        when(repo.existsById("M1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteMobileById("M1"))
                .isInstanceOf(MobileNotFoundException.class);

        verify(repo).existsById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteMobileById_ok_deletes() {
        when(repo.existsById("M1")).thenReturn(true);

        service.deleteMobileById("M1");

        verify(repo).existsById("M1");
        verify(repo).deleteById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteMobileById_dataAccess_becomesMobilePersistenceException() {
        when(repo.existsById("M1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("M1");

        assertThatThrownBy(() -> service.deleteMobileById("M1"))
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to delete command: M1");

        verify(repo).existsById("M1");
        verify(repo).deleteById("M1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllMobiles_ok_returnsCount() {
        when(repo.count()).thenReturn(4L);

        assertThat(service.deleteAllMobiles()).isEqualTo(4L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllMobiles_dataAccess_becomesMobilePersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllMobiles())
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to delete all commands");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllMobilesFallback_returnsEmptyList() throws Exception {
        var m = MobileService.class.getDeclaredMethod("getAllMobilesFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<MobileDocument> out = (List<MobileDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getMobileByIdFallback_throwsMobilePersistenceException() throws Exception {
        var m = MobileService.class.getDeclaredMethod("getMobileByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "M1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("MobileDocument lookup temporarily unavailable: M1");

        verifyNoInteractions(repo);
    }
}
