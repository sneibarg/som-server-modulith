package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.domain.player.PlayerAccount;
import org.springy.som.modulith.exception.player.InvalidPlayerException;
import org.springy.som.modulith.exception.player.PlayerNotFoundException;
import org.springy.som.modulith.exception.player.PlayerPersistenceException;
import org.springy.som.modulith.repository.PlayerAccountRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {
    private final String playerAccountIdMissing = "ROM account id must be provided";
    private final String playerAccountMissing = "ROM account must be provided";
    @Mock
    private PlayerAccountRepository repo;
    private PlayerService service;

    @BeforeEach
    void setUp() {
        service = new PlayerService(repo);
    }

    @Test
    void getAllPlayerAccounts_ok() {
        List<PlayerAccount> accounts = List.of(mock(PlayerAccount.class), mock(PlayerAccount.class));
        when(repo.findAll()).thenReturn(accounts);

        assertThat(service.getAllPlayerAccounts()).isSameAs(accounts);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerAccountByName_delegates() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(repo.findPlayerAccountByName("Scott")).thenReturn(account);

        assertThat(service.getPlayerAccountByName("Scott")).isSameAs(account);

        verify(repo).findPlayerAccountByName("Scott");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getPlayerAccountById_delegates() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(repo.findPlayerAccountById("P1")).thenReturn(account);

        assertThat(service.getPlayerAccountById("P1")).isSameAs(account);

        verify(repo).findPlayerAccountById("P1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createPlayerAccount_null_becomesInvalidPlayerException() {
        assertThatThrownBy(() -> service.createPlayerAccount(null))
                .isInstanceOf(InvalidPlayerException.class)
                .hasMessageContaining(playerAccountMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createPlayerAccount_blankId_becomesInvalidPlayerException() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(account.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createPlayerAccount(account))
                .isInstanceOf(InvalidPlayerException.class)
                .hasMessageContaining(playerAccountIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createPlayerAccount_ok_saves() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(account.getId()).thenReturn("P1");
        when(repo.save(account)).thenReturn(account);

        assertThat(service.createPlayerAccount(account)).isSameAs(account);

        verify(repo).save(account);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createPlayerAccount_dataAccess_becomesPlayerPersistenceException() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(account.getId()).thenReturn("P1");
        when(repo.save(account)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createPlayerAccount(account))
                .isInstanceOf(PlayerPersistenceException.class)
                .hasMessageContaining("Failed to create player account");

        verify(repo).save(account);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createPlayerAccount_dataAccess_safeIdExceptionPath_becomesPlayerPersistenceException() {
        PlayerAccount account = mock(PlayerAccount.class);
        when(account.getId()).thenReturn("P1").thenThrow(new RuntimeException("boom"));
        when(repo.save(account)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createPlayerAccount(account))
                .isInstanceOf(PlayerPersistenceException.class)
                .hasMessageContaining("Failed to create player account");

        verify(repo).save(account);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void savePlayerAccountForId_blankId_becomesInvalidPlayerException() {
        PlayerAccount account = mock(PlayerAccount.class);

        assertThatThrownBy(() -> service.savePlayerAccountForId(" ", account))
                .isInstanceOf(InvalidPlayerException.class)
                .hasMessageContaining(playerAccountIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerAccountForId_nullAccount_becomesInvalidPlayerException() {
        assertThatThrownBy(() -> service.savePlayerAccountForId("P1", null))
                .isInstanceOf(InvalidPlayerException.class)
                .hasMessageContaining(playerAccountMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void savePlayerAccountForId_ok_savesByLookup() {
        PlayerAccount input = mock(PlayerAccount.class);
        when(input.getId()).thenReturn("P1");

        PlayerAccount existing = mock(PlayerAccount.class);
        when(repo.findPlayerAccountById("P1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.savePlayerAccountForId("P1", input)).isSameAs(existing);

        verify(repo).findPlayerAccountById("P1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerAccountById_blankId_becomesInvalidPlayerException() {
        assertThatThrownBy(() -> service.deletePlayerAccountById(""))
                .isInstanceOf(InvalidPlayerException.class)
                .hasMessageContaining(playerAccountIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deletePlayerAccountById_notFound_becomesPlayerNotFoundException() {
        when(repo.existsById("P1")).thenReturn(false);

        assertThatThrownBy(() -> service.deletePlayerAccountById("P1"))
                .isInstanceOf(PlayerNotFoundException.class);

        verify(repo).existsById("P1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerAccountById_ok_deletes() {
        when(repo.existsById("P1")).thenReturn(true);

        service.deletePlayerAccountById("P1");

        verify(repo).existsById("P1");
        verify(repo).deleteById("P1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deletePlayerAccountById_dataAccess_becomesPlayerPersistenceException() {
        when(repo.existsById("P1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("P1");

        assertThatThrownBy(() -> service.deletePlayerAccountById("P1"))
                .isInstanceOf(PlayerPersistenceException.class)
                .hasMessageContaining("Service unavailable Failed to delete player account: P1 org.springframework.dao.DataAccessResourceFailureException: db down");

        verify(repo).existsById("P1");
        verify(repo).deleteById("P1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllPlayerAccounts_ok_returnsCount() {
        when(repo.count()).thenReturn(7L);

        assertThat(service.deleteAllPlayerAccounts()).isEqualTo(7L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllPlayerAccounts_dataAccess_becomesPlayerPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllPlayerAccounts())
                .isInstanceOf(PlayerPersistenceException.class)
                .hasMessageContaining("Service unavailable Failed to delete all player accounts org.springframework.dao.DataAccessResourceFailureException: db down");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllPlayerAccountsFallback_returnsEmptyList() throws Exception {
        var m = PlayerService.class.getDeclaredMethod("getAllPlayerAccountsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<PlayerAccount> out = (List<PlayerAccount>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getPlayerAccountByIdFallback_throwsPlayerPersistenceException() throws Exception {
        var m = PlayerService.class.getDeclaredMethod("getPlayerAccountByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "P1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(PlayerPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: P1");

        verifyNoInteractions(repo);
    }
}
