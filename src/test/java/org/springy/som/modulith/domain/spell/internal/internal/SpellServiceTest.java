package org.springy.som.modulith.domain.spell.internal.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.domain.spell.api.SpellDeletedEvent;
import org.springy.som.modulith.domain.spell.internal.InvalidSpellException;
import org.springy.som.modulith.domain.spell.internal.SpellDocument;
import org.springy.som.modulith.domain.spell.internal.SpellNotFoundException;
import org.springy.som.modulith.domain.spell.internal.SpellPersistenceException;
import org.springy.som.modulith.domain.spell.internal.SpellRepository;
import org.springy.som.modulith.domain.spell.internal.SpellService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpellServiceTest {
    private final String spellIdMissing = "ROM spell id must be provided";
    private final String spellMissing = "ROM spell must be provided";
    private final String spellNameMissing = "ROM spell name must be provided";

    @Mock
    private SpellRepository repo;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private SpellService service;

    @BeforeEach
    void setUp() {
        service = new SpellService(repo, eventPublisher);
    }

    @Test
    void getAllSpells_ok() {
        List<SpellDocument> spellDocuments = List.of(mock(SpellDocument.class), mock(SpellDocument.class));
        when(repo.findAll()).thenReturn(spellDocuments);

        assertThat(service.getAllSpells()).isSameAs(spellDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSpells_dataAccess_becomesSpellPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getAllSpells())
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to load Spells");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellById_blankId_becomesInvalidSpellException() {
        assertThatThrownBy(() -> service.getSpellById(" "))
                .isInstanceOf(InvalidSpellException.class)
                .hasMessageContaining(spellIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void getSpellById_notFound_becomesSpellNotFoundException() {
        when(repo.findSpellById("SK1")).thenReturn(null);

        assertThatThrownBy(() -> service.getSpellById("SK1"))
                .isInstanceOf(SpellNotFoundException.class);

        verify(repo).findSpellById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellById_ok_returnsEntity() {
        SpellDocument spell = mock(SpellDocument.class);
        when(repo.findSpellById("SK1")).thenReturn(spell);

        assertThat(service.getSpellById("SK1")).isSameAs(spell);

        verify(repo).findSpellById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellById_dataAccess_becomesSpellPersistenceException() {
        when(repo.findSpellById("SK1")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getSpellById("SK1"))
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to load Spell: SK1");

        verify(repo).findSpellById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellByName_blankName_becomesInvalidSpellException() {
        assertThatThrownBy(() -> service.getSpellByName(" "))
                .isInstanceOf(InvalidSpellException.class)
                .hasMessageContaining(spellNameMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void getSpellByName_notFound_becomesSpellNotFoundException() {
        when(repo.findSpellByName("fireball")).thenReturn(null);

        assertThatThrownBy(() -> service.getSpellByName("fireball"))
                .isInstanceOf(SpellNotFoundException.class);

        verify(repo).findSpellByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellByName_ok_returnsEntity() {
        SpellDocument spell = mock(SpellDocument.class);
        when(repo.findSpellByName("fireball")).thenReturn(spell);

        assertThat(service.getSpellByName("fireball")).isSameAs(spell);

        verify(repo).findSpellByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getSpellByName_dataAccess_becomesSpellPersistenceException() {
        when(repo.findSpellByName("fireball")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.getSpellByName("fireball"))
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to load Spell: fireball");

        verify(repo).findSpellByName("fireball");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpell_null_becomesInvalidSpellException() {
        assertThatThrownBy(() -> service.createSpell(null))
                .isInstanceOf(InvalidSpellException.class)
                .hasMessageContaining(spellIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSpell_blankId_becomesInvalidSpellException() {
        SpellDocument spellDocument = mock(SpellDocument.class);
        when(spellDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createSpell(spellDocument))
                .isInstanceOf(InvalidSpellException.class)
                .hasMessageContaining(spellIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createSpell_ok_saves() {
        SpellDocument spellDocument = mock(SpellDocument.class);
        when(spellDocument.getId()).thenReturn("SK1");
        when(repo.save(spellDocument)).thenReturn(spellDocument);

        assertThat(service.createSpell(spellDocument)).isSameAs(spellDocument);

        verify(repo).save(spellDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createSpell_dataAccess_becomesSpellPersistenceException() {
        SpellDocument spellDocument = mock(SpellDocument.class);
        when(spellDocument.getId()).thenReturn("SK1");
        when(repo.save(spellDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createSpell(spellDocument))
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to create SpellDocument");

        verify(repo).save(spellDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSpellForId_blankId_becomesNullPointerException() {
        SpellDocument spellDocument = mock(SpellDocument.class);
        when(repo.findSpellById(" ")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSpellForId(" ", spellDocument))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSpellById(" ");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSpellForId_nullSpell_becomesNullPointerException() {
        when(repo.findSpellById("SK1")).thenReturn(null);

        assertThatThrownBy(() -> service.saveSpellForId("SK1", null))
                .isInstanceOf(NullPointerException.class);

        verify(repo).findSpellById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveSpellForId_ok_savesByLookup() {
        SpellDocument input = mock(SpellDocument.class);
        SpellDocument existing = mock(SpellDocument.class);

        when(existing.getId()).thenReturn("SK1");
        when(repo.findSpellById("SK1")).thenReturn(existing);
        when(repo.save(input)).thenReturn(input);

        assertThat(service.saveSpellForId("SK1", input)).isSameAs(input);

        InOrder inOrder = inOrder(repo);
        inOrder.verify(repo).findSpellById("SK1");
        verify(existing, times(2)).getId();
        inOrder.verify(repo).save(input);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSpellById_blankId_becomesInvalidSpellException() {
        assertThatThrownBy(() -> service.deleteSpellById(""))
                .isInstanceOf(InvalidSpellException.class)
                .hasMessageContaining(spellIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteSpellById_notFound_becomesSpellNotFoundException() {
        when(repo.existsById("SK1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteSpellById("SK1"))
                .isInstanceOf(SpellNotFoundException.class);

        verify(repo).existsById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteSpellById_ok_deletes() {
        when(repo.existsById("SK1")).thenReturn(true);

        service.deleteSpellById("SK1");

        InOrder inOrder = inOrder(repo, eventPublisher);
        inOrder.verify(repo).existsById("SK1");
        inOrder.verify(repo).deleteById("SK1");
        inOrder.verify(eventPublisher).publishEvent(any(SpellDeletedEvent.class));
        verifyNoMoreInteractions(repo, eventPublisher);
    }

    @Test
    void deleteSpellById_dataAccess_becomesSpellPersistenceException() {
        when(repo.existsById("SK1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("SK1");

        assertThatThrownBy(() -> service.deleteSpellById("SK1"))
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to delete Spell: SK1");

        verify(repo).existsById("SK1");
        verify(repo).deleteById("SK1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSpells_ok_returnsCount() {
        SpellDocument spell1 = mock(SpellDocument.class);
        when(spell1.getId()).thenReturn("SK1");
        SpellDocument spell2 = mock(SpellDocument.class);
        when(spell2.getId()).thenReturn("SK2");
        List<SpellDocument> spells = List.of(spell1, spell2);
        when(repo.findAll()).thenReturn(spells);

        assertThat(service.deleteAllSpells()).isEqualTo(2L);

        verify(repo).findAll();
        verify(repo).deleteAll();
        verify(eventPublisher, times(2)).publishEvent(any(SpellDeletedEvent.class));
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllSpells_dataAccess_becomesSpellPersistenceException() {
        when(repo.findAll()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllSpells())
                .isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("Failed to delete all Spells");

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllSpellsFallback_returnsEmptyList() throws Exception {
        var m = SpellService.class.getDeclaredMethod("getAllSpellsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<SpellDocument> out = (List<SpellDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getSpellByIdFallback_throwsSpellPersistenceException() throws Exception {
        var m = SpellService.class.getDeclaredMethod("getSpellByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "SK1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(SpellPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: SK1");

        verifyNoInteractions(repo);
    }
}
