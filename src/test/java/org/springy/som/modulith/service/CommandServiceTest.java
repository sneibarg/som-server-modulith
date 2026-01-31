package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.exception.clazz.RomClassNotFoundException;
import org.springy.som.modulith.exception.clazz.RomClassPersistenceException;
import org.springy.som.modulith.exception.command.CommandPersistenceException;
import org.springy.som.modulith.exception.command.InvalidCommandException;
import org.springy.som.modulith.repository.CommandRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    private CommandRepository repo;
    private CommandService service;

    @BeforeEach
    void setUp() {
        repo = mock(CommandRepository.class);
        service = new CommandService(repo);
    }

    @Test
    void getAllCommands_returnsRepoResults() {
        List<Command> expected = List.of(mock(Command.class), mock(Command.class));
        when(repo.findAll()).thenReturn(expected);

        List<Command> actual = service.getAllCommands();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getCommandByName_returnsRepoResult() {
        Command expected = mock(Command.class);
        when(repo.findCommandByName("look")).thenReturn(expected);

        Command actual = service.getCommandByName("look");

        assertThat(actual).isSameAs(expected);
        verify(repo).findCommandByName("look");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getCommandById_returnsRepoResult() {
        Command expected = mock(Command.class);
        when(repo.findCommandById("C1")).thenReturn(expected);

        Command actual = service.getCommandById("C1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findCommandById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createCommand_nullCommand_throwsInvalid() {
        assertThatThrownBy(() -> service.createCommand(null))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createCommand_blankId_throwsInvalid() {
        Command c = mock(Command.class);
        when(c.getId()).thenReturn("  ");

        assertThatThrownBy(() -> service.createCommand(c))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createCommand_success_savesAndReturns() {
        Command input = mock(Command.class);
        when(input.getId()).thenReturn("C1");
        when(repo.save(input)).thenReturn(input);

        Command out = service.createCommand(input);

        assertThat(out).isSameAs(input);
        verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createCommand_dataAccess_becomesRomClassPersistenceException() {
        Command c = mock(Command.class);
        when(c.getId()).thenReturn("C1");
        when(repo.save(c)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createCommand(c))
                .isInstanceOf(RomClassPersistenceException.class)
                .hasMessageContaining("Failed to create command");

        verify(repo).save(c);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createCommand_dataAccess_safeIdExceptionPath_becomesRomClassPersistenceException() {
        Command c = mock(Command.class);


        when(c.getId()).thenReturn("C1").thenThrow(new RuntimeException("boom"));
        when(repo.save(c)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createCommand(c))
                .isInstanceOf(RomClassPersistenceException.class)
                .hasMessageContaining("Failed to create command");

        verify(repo).save(c);
        verifyNoMoreInteractions(repo);
    }


    @Test
    void saveCommandForId_blankId_throwsInvalid() {
        Command c = mock(Command.class);

        assertThatThrownBy(() -> service.saveCommandForId("  ", c))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveCommandForId_nullCommand_throwsInvalid() {
        assertThatThrownBy(() -> service.saveCommandForId("C1", null))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveCommandForId_blankCommandId_throwsInvalid() {
        Command c = mock(Command.class);
        when(c.getId()).thenReturn("");

        assertThatThrownBy(() -> service.saveCommandForId("C1", c))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveCommandForId_success_savesLookupResult() {
        Command payload = mock(Command.class);
        when(payload.getId()).thenReturn("C1");

        Command existing = mock(Command.class);
        when(repo.findCommandById("C1")).thenReturn(existing);

        Command saved = mock(Command.class);
        when(repo.save(existing)).thenReturn(saved);

        Command out = service.saveCommandForId("C1", payload);

        assertThat(out).isSameAs(saved);
        verify(repo).findCommandById("C1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteCommandById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deleteCommandById(" "))
                .isInstanceOf(InvalidCommandException.class)
                .hasMessageContaining("Command id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void deleteCommandById_notFound_propagatesRomClassNotFound() {
        when(repo.existsById("C1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteCommandById("C1"))
                .isInstanceOf(RomClassNotFoundException.class);

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteCommandById_success_deletes() {
        when(repo.existsById("C1")).thenReturn(true);

        service.deleteCommandById("C1");

        verify(repo).existsById("C1");
        verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteCommandById_dataAccess_becomesCommandPersistenceException() {
        when(repo.existsById("C1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("C1");

        assertThatThrownBy(() -> service.deleteCommandById("C1"))
                .isInstanceOf(CommandPersistenceException.class)
                .hasMessageContaining("Failed to delete command: C1");

        verify(repo).existsById("C1");
        verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllCommands_success_returnsCount() {
        when(repo.count()).thenReturn(7L);

        long out = service.deleteAllCommands();

        assertThat(out).isEqualTo(7L);
        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllCommands_dataAccess_becomesCommandPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllCommands())
                .isInstanceOf(CommandPersistenceException.class)
                .hasMessageContaining("Failed to delete all commands");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllCommandsFallback_returnsEmptyList() throws Exception {
        Method m = CommandService.class.getDeclaredMethod("getAllCommandsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Command> out = (List<Command>) m.invoke(service, new RuntimeException("boom"));

        assertThat(out).isNotNull().isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getRomClassByIdFallback_throwsCommandPersistenceException() throws Exception {
        Method m = CommandService.class.getDeclaredMethod("getRomClassByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        Throwable t = new RuntimeException("boom");
        Throwable thrown = catchThrowable(() -> {
            try {
                m.invoke(service, "C1", t);
            } catch (Exception e) {
                throw e.getCause() != null ? (RuntimeException) e.getCause() : e;
            }
        });

        assertThat(thrown)
                .isInstanceOf(CommandPersistenceException.class)
                .hasMessageContaining("Command lookup temporarily unavailable: C1");

        verifyNoInteractions(repo);
    }
}
