package org.springy.som.modulith.domain.command.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelpServiceTest {
    private final String helpIdMissing = "ROM help id must be provided";
    private final String helpMissing = "ROM help not found in repository";
    private final String helpNotProvided = "ROM help must be provided";
    private HelpRepository repo;
    private HelpService service;

    @BeforeEach
    void setUp() {
        repo = mock(HelpRepository.class);
        service = new HelpService(repo);
    }

    @Test
    void getAllHelps_returnsRepoResults() {
        List<HelpDocument> expected = List.of(mock(HelpDocument.class), mock(HelpDocument.class));
        when(repo.findAll()).thenReturn(expected);

        List<HelpDocument> actual = service.getAllHelps();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getHelpByKeyword_returnsRepoResult() {
        HelpDocument expected = mock(HelpDocument.class);
        when(repo.findHelpByKeyword("combat")).thenReturn(expected);

        HelpDocument actual = service.getHelpByKeyword("combat");

        assertThat(actual).isSameAs(expected);
        verify(repo).findHelpByKeyword("combat");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getHelpById_returnsRepoResult() {
        HelpDocument expected = mock(HelpDocument.class);
        when(repo.findHelpById("H1")).thenReturn(expected);

        HelpDocument actual = service.getHelpById("H1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findHelpById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createHelp_nullHelp_throwsInvalidHelpException() {
        assertThatThrownBy(() -> service.createHelp(null))
                .isInstanceOf(InvalidHelpException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void createHelp_blankId_throwsInvalid() {
        HelpDocument h = mock(HelpDocument.class);
        when(h.getId()).thenReturn("  ");

        assertThatThrownBy(() -> service.createHelp(h))
                .isInstanceOf(InvalidHelpException.class)
                .hasMessageContaining(helpIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createHelp_success_savesAndReturns() {
        HelpDocument input = mock(HelpDocument.class);
        when(input.getId()).thenReturn("H1");
        when(repo.save(input)).thenReturn(input);

        HelpDocument out = service.createHelp(input);

        assertThat(out).isSameAs(input);
        verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createHelp_dataAccess_becomesHelpPersistenceException() {
        HelpDocument h = mock(HelpDocument.class);
        when(h.getId()).thenReturn("H1");
        when(repo.save(h)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createHelp(h))
                .isInstanceOf(HelpPersistenceException.class)
                .hasMessageContaining("Failed to create help");

        verify(repo).save(h);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createHelp_dataAccess_safeIdExceptionPath_becomesHelpPersistenceException() {
        HelpDocument help = mock(HelpDocument.class);

        when(help.getId()).thenReturn("H1").thenThrow(new RuntimeException("boom"));
        when(repo.save(help)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createHelp(help))
                .isInstanceOf(HelpPersistenceException.class)
                .hasMessageContaining("Failed to create help");

        verify(repo).save(help);
        verifyNoMoreInteractions(repo);
    }


    @Test
    void saveHelpForId_blankId_throwsNullPointerException() {
        HelpDocument h = mock(HelpDocument.class);
        when(repo.findHelpById("  ")).thenReturn(null);

        assertThatThrownBy(() -> service.saveHelpForId("  ", h))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findHelpById("  ");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveHelpForId_nullHelp_throwsNullPointerException() {
        when(repo.findHelpById("H1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveHelpForId("H1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findHelpById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveHelpForId_blankHelpId_throwsNullPointerException() {
        HelpDocument input = mock(HelpDocument.class);
        when(repo.findHelpById("H1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveHelpForId("H1", input))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findHelpById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveHelpForId_success_savesLookupResult() {
        HelpDocument payload = mock(HelpDocument.class);
        HelpDocument existing = mock(HelpDocument.class);

        when(existing.getId()).thenReturn("H1");
        when(repo.findHelpById("H1")).thenReturn(existing);
        when(repo.save(payload)).thenReturn(payload);

        HelpDocument out = service.saveHelpForId("H1", payload);

        assertThat(out).isSameAs(payload);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findHelpById("H1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(payload);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteHelpById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deleteHelpById(" "))
                .isInstanceOf(InvalidHelpException.class)
                .hasMessageContaining(helpIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteHelpById_notFound_propagatesHelpNotFound() {
        when(repo.existsById("H1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteHelpById("H1"))
                .isInstanceOf(HelpNotFoundException.class);

        verify(repo).existsById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteHelpById_success_deletes() {
        when(repo.existsById("H1")).thenReturn(true);

        service.deleteHelpById("H1");

        verify(repo).existsById("H1");
        verify(repo).deleteById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteHelpById_dataAccess_becomesHelpPersistenceException() {
        when(repo.existsById("H1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("H1");

        assertThatThrownBy(() -> service.deleteHelpById("H1"))
                .isInstanceOf(HelpPersistenceException.class)
                .hasMessageContaining("Failed to delete help: H1");

        verify(repo).existsById("H1");
        verify(repo).deleteById("H1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllHelps_success_returnsCount() {
        when(repo.count()).thenReturn(7L);

        long out = service.deleteAllHelps();

        assertThat(out).isEqualTo(7L);
        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllHelps_dataAccess_becomesHelpPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllHelps())
                .isInstanceOf(HelpPersistenceException.class)
                .hasMessageContaining("Failed to delete all helps");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllHelpsFallback_returnsEmptyList() throws Exception {
        Method m = HelpService.class.getDeclaredMethod("getAllHelpsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<HelpDocument> out = (List<HelpDocument>) m.invoke(service, new RuntimeException("boom"));

        assertThat(out).isNotNull().isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getHelpByIdFallback_throwsHelpPersistenceException() throws Exception {
        Method m = HelpService.class.getDeclaredMethod("getHelpByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        Throwable t = new RuntimeException("boom");
        Throwable thrown = catchThrowable(() -> {
            try {
                m.invoke(service, "H1", t);
            } catch (Exception e) {
                throw e.getCause() != null ? (RuntimeException) e.getCause() : e;
            }
        });

        assertThat(thrown)
                .isInstanceOf(HelpPersistenceException.class)
                .hasMessageContaining("HelpDocument lookup temporarily unavailable: H1");

        verifyNoInteractions(repo);
    }
}
