package org.springy.som.modulith.domain.skill.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.domain.skill.api.SkillDeletedEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    private final String skillIdMissing = "ROM skill id must be provided";
    private final String skillMissing = "ROM skill must be provided";
    private final String skillNameMissing = "ROM skill name must be provided";

    @Mock
    private SkillRepository repo;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private SkillService service;

    @BeforeEach
    void setUp() {
        service = new SkillService(repo, eventPublisher);
    }

    @Test
    void getAllSkills_ok() {
        List<SkillDocument> skillDocuments = List.of(mock(SkillDocument.class), mock(SkillDocument.class));
        when(repo.findAll()).thenReturn(skillDocuments);

        assertThat(service.getAllSkills()).isSameAs(skillDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSkills_dataAccess_becomesSkillPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getAllSkills())
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to load Skills");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillById_blankId_becomesInvalidSkillException() {
        assertThatThrownBy(() -> service.getSkillById(" "))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining(skillIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void getSkillById_notFound_becomesSkillNotFoundException() {
        when(repo.findSkillById("SK1")).thenReturn(null);

        assertThatThrownBy(() -> service.getSkillById("SK1"))
                .isInstanceOf(SkillNotFoundException.class);

        verify(repo).findSkillById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillById_ok_returnsEntity() {
        SkillDocument skill = mock(SkillDocument.class);
        when(repo.findSkillById("SK1")).thenReturn(skill);

        assertThat(service.getSkillById("SK1")).isSameAs(skill);

        verify(repo).findSkillById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillById_dataAccess_becomesSkillPersistenceException() {
        when(repo.findSkillById("SK1")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getSkillById("SK1"))
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to load Skill: SK1");

        verify(repo).findSkillById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillByName_blankName_becomesInvalidSkillException() {
        assertThatThrownBy(() -> service.getSkillByName(" "))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining(skillNameMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void getSkillByName_notFound_becomesSkillNotFoundException() {
        when(repo.findSkillByName("fireball")).thenReturn(null);

        assertThatThrownBy(() -> service.getSkillByName("fireball"))
                .isInstanceOf(SkillNotFoundException.class);

        verify(repo).findSkillByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillByName_ok_returnsEntity() {
        SkillDocument skill = mock(SkillDocument.class);
        when(repo.findSkillByName("fireball")).thenReturn(skill);

        assertThat(service.getSkillByName("fireball")).isSameAs(skill);

        verify(repo).findSkillByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSkillByName_dataAccess_becomesSkillPersistenceException() {
        when(repo.findSkillByName("fireball")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getSkillByName("fireball"))
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to load Skill: fireball");

        verify(repo).findSkillByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSkill_null_becomesInvalidSkillException() {
        assertThatThrownBy(() -> service.createSkill(null))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining(skillIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSkill_blankId_becomesInvalidSkillException() {
        SkillDocument skillDocument = mock(SkillDocument.class);
        when(skillDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createSkill(skillDocument))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining(skillIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSkill_ok_saves() {
        SkillDocument skillDocument = mock(SkillDocument.class);
        when(skillDocument.getId()).thenReturn("SK1");
        when(repo.save(skillDocument)).thenReturn(skillDocument);

        assertThat(service.createSkill(skillDocument)).isSameAs(skillDocument);

        verify(repo).save(skillDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSkill_dataAccess_becomesSkillPersistenceException() {
        SkillDocument skillDocument = mock(SkillDocument.class);
        when(skillDocument.getId()).thenReturn("SK1");
        when(repo.save(skillDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSkill(skillDocument))
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to create SkillDocument");

        verify(repo).save(skillDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSkillForId_blankId_becomesNullPointerException() {
        SkillDocument skillDocument = mock(SkillDocument.class);
        when(repo.findSkillById(" ")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSkillForId(" ", skillDocument))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSkillById(" ");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSkillForId_nullSkill_becomesNullPointerException() {
        when(repo.findSkillById("SK1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSkillForId("SK1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSkillById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSkillForId_ok_savesByLookup() {
        SkillDocument input = mock(SkillDocument.class);
        SkillDocument existing = mock(SkillDocument.class);

        when(existing.getId()).thenReturn("SK1");
        when(repo.findSkillById("SK1")).thenReturn(existing);
        when(repo.save(input)).thenReturn(input);

        assertThat(service.saveSkillForId("SK1", input)).isSameAs(input);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findSkillById("SK1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSkillById_blankId_becomesInvalidSkillException() {
        assertThatThrownBy(() -> service.deleteSkillById(""))
                .isInstanceOf(InvalidSkillException.class)
                .hasMessageContaining(skillIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteSkillById_notFound_becomesSkillNotFoundException() {
        when(repo.existsById("SK1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteSkillById("SK1"))
                .isInstanceOf(SkillNotFoundException.class);

        verify(repo).existsById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSkillById_ok_deletes() {
        when(repo.existsById("SK1")).thenReturn(true);

        service.deleteSkillById("SK1");

        InOrder inOrder = inOrder(repo, eventPublisher);
        inOrder.verify(repo).existsById("SK1");
        inOrder.verify(repo).deleteById("SK1");
        inOrder.verify(eventPublisher).publishEvent(any(SkillDeletedEvent.class));
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deleteSkillById_dataAccess_becomesSkillPersistenceException() {
        when(repo.existsById("SK1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("SK1");

        assertThatThrownBy(() -> service.deleteSkillById("SK1"))
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to delete Skill: SK1");

        verify(repo).existsById("SK1");
        verify(repo).deleteById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSkills_ok_returnsCount() {
        SkillDocument skill1 = mock(SkillDocument.class);
        when(skill1.getId()).thenReturn("SK1");
        SkillDocument skill2 = mock(SkillDocument.class);
        when(skill2.getId()).thenReturn("SK2");
        List<SkillDocument> skills = List.of(skill1, skill2);
        when(repo.findAll()).thenReturn(skills);

        assertThat(service.deleteAllSkills()).isEqualTo(2L);

        verify(repo).findAll();
        verify(repo).deleteAll();
        verify(eventPublisher, times(2)).publishEvent(any(SkillDeletedEvent.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSkills_dataAccess_becomesSkillPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllSkills())
                .isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("Failed to delete all Skills");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSkillsFallback_returnsEmptyList() throws Exception {
        var m = SkillService.class.getDeclaredMethod("getAllSkillsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SkillDocument> out = (List<SkillDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getSkillByIdFallback_throwsSkillPersistenceException() throws Exception {
        var m = SkillService.class.getDeclaredMethod("getSkillByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "SK1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(SkillPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: SK1");

        verifyNoInteractions(repo);
    }
}
