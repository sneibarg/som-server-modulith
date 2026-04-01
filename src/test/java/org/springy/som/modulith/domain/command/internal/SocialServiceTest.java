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
class SocialServiceTest {
    private final String socialIdMissing = "ROM social id must be provided";
    private final String socialMissing = "ROM social not found in repository";
    private final String socialNotProvided = "ROM social must be provided";
    private SocialRepository repo;
    private SocialService service;

    @BeforeEach
    void setUp() {
        repo = mock(SocialRepository.class);
        service = new SocialService(repo);
    }

    @Test
    void getAllSocials_returnsRepoResults() {
        List<SocialDocument> expected = List.of(mock(SocialDocument.class), mock(SocialDocument.class));
        when(repo.findAll()).thenReturn(expected);

        List<SocialDocument> actual = service.getAllSocials();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSocialByName_returnsRepoResult() {
        SocialDocument expected = mock(SocialDocument.class);
        when(repo.findSocialByName("look")).thenReturn(expected);

        SocialDocument actual = service.getSocialByName("look");

        assertThat(actual).isSameAs(expected);
        verify(repo).findSocialByName("look");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSocialById_returnsRepoResult() {
        SocialDocument expected = mock(SocialDocument.class);
        when(repo.findSocialById("C1")).thenReturn(expected);

        SocialDocument actual = service.getSocialById("C1");

        assertThat(actual).isSameAs(expected);
        verify(repo).findSocialById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSocial_nullSocial_throwsInvalidSocialException() {
        assertThatThrownBy(() -> service.createSocial(null))
                .isInstanceOf(InvalidSocialException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void createSocial_blankId_throwsInvalid() {
        SocialDocument c = mock(SocialDocument.class);
        when(c.getId()).thenReturn("  ");

        assertThatThrownBy(() -> service.createSocial(c))
                .isInstanceOf(InvalidSocialException.class)
                .hasMessageContaining(socialIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSocial_success_savesAndReturns() {
        SocialDocument input = mock(SocialDocument.class);
        when(input.getId()).thenReturn("C1");
        when(repo.save(input)).thenReturn(input);

        SocialDocument out = service.createSocial(input);

        assertThat(out).isSameAs(input);
        verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSocial_dataAccess_becomesSocialPersistenceException() {
        SocialDocument c = mock(SocialDocument.class);
        when(c.getId()).thenReturn("C1");
        when(repo.save(c)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSocial(c))
                .isInstanceOf(SocialPersistenceException.class)
                .hasMessageContaining("Failed to create social");

        verify(repo).save(c);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSocial_dataAccess_safeIdExceptionPath_becomesSocialPersistenceException() {
        SocialDocument social = mock(SocialDocument.class);

        when(social.getId()).thenReturn("S1").thenThrow(new RuntimeException("boom"));
        when(repo.save(social)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSocial(social))
                .isInstanceOf(SocialPersistenceException.class)
                .hasMessageContaining("Failed to create social");

        verify(repo).save(social);
        verifyNoMoreInteractions(repo);
    }


    @Test
    void saveSocialForId_blankId_throwsNullPointerException() {
        SocialDocument c = mock(SocialDocument.class);
        when(repo.findSocialById("  ")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSocialForId("  ", c))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSocialById("  ");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSocialForId_nullSocial_throwsNullPointerException() {
        when(repo.findSocialById("C1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSocialForId("C1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSocialById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSocialForId_blankSocialId_throwsNullPointerException() {
        SocialDocument input = mock(SocialDocument.class);
        when(repo.findSocialById("C1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSocialForId("C1", input))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSocialById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSocialForId_success_savesLookupResult() {
        SocialDocument payload = mock(SocialDocument.class);
        SocialDocument existing = mock(SocialDocument.class);

        when(existing.getId()).thenReturn("C1");
        when(repo.findSocialById("C1")).thenReturn(existing);
        when(repo.save(payload)).thenReturn(payload);

        SocialDocument out = service.saveSocialForId("C1", payload);

        assertThat(out).isSameAs(payload);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findSocialById("C1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(payload);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSocialById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deleteSocialById(" "))
                .isInstanceOf(InvalidSocialException.class)
                .hasMessageContaining(socialIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteSocialById_notFound_propagatesSocialNotFound() {
        when(repo.existsById("C1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteSocialById("C1"))
                .isInstanceOf(SocialNotFoundException.class);

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSocialById_success_deletes() {
        when(repo.existsById("C1")).thenReturn(true);

        service.deleteSocialById("C1");

        verify(repo).existsById("C1");
        verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSocialById_dataAccess_becomesSocialPersistenceException() {
        when(repo.existsById("C1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("C1");

        assertThatThrownBy(() -> service.deleteSocialById("C1"))
                .isInstanceOf(SocialPersistenceException.class)
                .hasMessageContaining("Failed to delete social: C1");

        verify(repo).existsById("C1");
        verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSocials_success_returnsCount() {
        when(repo.count()).thenReturn(7L);

        long out = service.deleteAllSocials();

        assertThat(out).isEqualTo(7L);
        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSocials_dataAccess_becomesSocialPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllSocials())
                .isInstanceOf(SocialPersistenceException.class)
                .hasMessageContaining("Failed to delete all socials");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSocialsFallback_returnsEmptyList() throws Exception {
        Method m = SocialService.class.getDeclaredMethod("getAllSocialsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SocialDocument> out = (List<SocialDocument>) m.invoke(service, new RuntimeException("boom"));

        assertThat(out).isNotNull().isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getSocialByIdFallback_throwsSocialPersistenceException() throws Exception {
        Method m = SocialService.class.getDeclaredMethod("getSocialByIdFallback", String.class, Throwable.class);
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
                .isInstanceOf(SocialPersistenceException.class)
                .hasMessageContaining("SocialDocument lookup temporarily unavailable: C1");

        verifyNoInteractions(repo);
    }
}
