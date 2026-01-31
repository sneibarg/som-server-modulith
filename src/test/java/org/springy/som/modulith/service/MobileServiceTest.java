package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.domain.mobile.Mobile;
import org.springy.som.modulith.exception.mobile.InvalidMobileException;
import org.springy.som.modulith.exception.mobile.MobileNotFoundException;
import org.springy.som.modulith.exception.mobile.MobilePersistenceException;
import org.springy.som.modulith.repository.MobileRepository;
import org.springframework.dao.DataAccessResourceFailureException;

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
        List<Mobile> mobiles = List.of(mock(Mobile.class), mock(Mobile.class));
        when(repo.findAll()).thenReturn(mobiles);

        assertThat(service.getAllMobiles()).isSameAs(mobiles);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getMobileByName_delegates() {
        Mobile mobile = mock(Mobile.class);
        when(repo.findMobileByName("orc")).thenReturn(mobile);

        assertThat(service.getMobileByName("orc")).isSameAs(mobile);

        verify(repo).findMobileByName("orc");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getMobileById_delegates() {
        Mobile mobile = mock(Mobile.class);
        when(repo.findMobileById("M1")).thenReturn(mobile);

        assertThat(service.getMobileById("M1")).isSameAs(mobile);

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
        Mobile mobile = mock(Mobile.class);
        when(mobile.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createMobile(mobile))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createMobile_ok_saves() {
        Mobile mobile = mock(Mobile.class);
        when(mobile.getId()).thenReturn("M1");
        when(repo.save(mobile)).thenReturn(mobile);

        assertThat(service.createMobile(mobile)).isSameAs(mobile);

        verify(repo).save(mobile);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createMobile_dataAccess_becomesMobilePersistenceException() {
        Mobile mobile = mock(Mobile.class);
        when(mobile.getId()).thenReturn("M1");
        when(repo.save(mobile)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createMobile(mobile))
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to create command");

        verify(repo).save(mobile);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createMobile_dataAccess_safeIdExceptionPath_becomesMobilePersistenceException() {
        Mobile mobile = mock(Mobile.class);
        when(mobile.getId()).thenReturn("M1").thenThrow(new RuntimeException("boom"));
        when(repo.save(mobile)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createMobile(mobile))
                .isInstanceOf(MobilePersistenceException.class)
                .hasMessageContaining("Failed to create command");

        verify(repo).save(mobile);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveMobileForId_blankId_becomesInvalidMobileException() {
        Mobile mobile = mock(Mobile.class);

        assertThatThrownBy(() -> service.saveMobileForId(" ", mobile))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveMobileForId_nullMobile_becomesInvalidMobileException() {
        assertThatThrownBy(() -> service.saveMobileForId("M1", null))
                .isInstanceOf(InvalidMobileException.class)
                .hasMessageContaining(mobileMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveMobileForId_ok_savesByLookup() {
        Mobile input = mock(Mobile.class);
        when(input.getId()).thenReturn("M1");

        Mobile existing = mock(Mobile.class);
        when(repo.findMobileById("M1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveMobileForId("M1", input)).isSameAs(existing);

        verify(repo).findMobileById("M1");
        verify(repo).save(existing);
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
        List<Mobile> out = (List<Mobile>) m.invoke(service, new RuntimeException("cb"));

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
                .hasMessageContaining("Mobile lookup temporarily unavailable: M1");

        verifyNoInteractions(repo);
    }
}
