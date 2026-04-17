package org.springy.som.modulith.domain.spell.internal.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springy.som.modulith.domain.spell.internal.InvalidSpellException;
import org.springy.som.modulith.domain.spell.internal.SpellApiExceptionHandler;
import org.springy.som.modulith.domain.spell.internal.SpellController;
import org.springy.som.modulith.domain.spell.internal.SpellDocument;
import org.springy.som.modulith.domain.spell.internal.SpellNotFoundException;
import org.springy.som.modulith.domain.spell.internal.SpellPersistenceException;
import org.springy.som.modulith.domain.spell.internal.SpellRepository;
import org.springy.som.modulith.domain.spell.internal.SpellService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpellController.class)
@Import(SpellApiExceptionHandler.class)
@WithMockUser
public class SpellControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpellService spellService;

    @MockitoBean
    SpellRepository spellRepository;

    @Mock
    SpellDocument spellDocument;

    @Test
    void getAllSpells_ok() throws Exception {
        when(spellService.getAllSpells()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/spells"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(spellService).getAllSpells();
    }

    @Test
    void getSpellById_notFound_becomes404ProblemDetail() throws Exception {
        when(spellService.getSpellById("SK1")).thenThrow(new SpellNotFoundException("SK1"));

        mockMvc.perform(get("/api/v1/spells/SK1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(spellService).getSpellById("SK1");
    }

    @Test
    void getSpellById_blankId_becomes400ProblemDetail() throws Exception {
        when(spellService.getSpellById(anyString())).thenThrow(new InvalidSpellException("ROM spell id must be provided"));
        when(spellRepository.findSpellById(anyString())).thenReturn(spellDocument);

        mockMvc.perform(get("/api/v1/spells/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(spellService).getSpellById("  ");
    }

    @Test
    void getAllSpells_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(spellService.getAllSpells()).thenThrow(new SpellPersistenceException("Failed to load spells"));

        mockMvc.perform(get("/api/v1/spells"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(spellService).getAllSpells();
    }

    @Test
    void deleteSpell_ok_returns204() throws Exception {
        doNothing().when(spellService).deleteSpellById("SK1");

        mockMvc.perform(delete("/api/v1/spells/SK1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(spellService).deleteSpellById("SK1");
    }

    @Test
    void deleteSpell_notFound_becomes404() throws Exception {
        when(spellService.getSpellById("SK1")).thenThrow(new SpellNotFoundException("SK1"));

        mockMvc.perform(get("/api/v1/spells/SK1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));

        verify(spellService).getSpellById("SK1");
    }

    @Test
    void createSpell_ok_returns201() throws Exception {
        SpellDocument spell = new SpellDocument();
        spell.setName("Fireball");

        SpellDocument saved = new SpellDocument();
        saved.setId("SK1");
        saved.setName("Fireball");

        when(spellService.createSpell(any(SpellDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/spells")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spell)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("SK1"))
                .andExpect(jsonPath("$.name").value("Fireball"));

        verify(spellService).createSpell(any(SpellDocument.class));
    }

    @Test
    void createSpell_invalidInput_becomes400() throws Exception {
        SpellDocument spell =  new SpellDocument();
        spell.setId("SK1");
        when(spellService.createSpell(any(SpellDocument.class))).thenThrow(new InvalidSpellException("ROM spell id must be provided"));

        mockMvc.perform(post("/api/v1/spells")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spell)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateSpell_ok_returns200() throws Exception {
        SpellDocument spell = new SpellDocument();
        spell.setId("SK1");
        spell.setName("Updated Fireball");

        when(spellService.saveSpellForId(eq("SK1"), any(SpellDocument.class))).thenReturn(spell);

        mockMvc.perform(put("/api/v1/spells/SK1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spell)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("SK1"))
                .andExpect(jsonPath("$.name").value("Updated Fireball"));

        verify(spellService).saveSpellForId(eq("SK1"), any(SpellDocument.class));
    }

    @Test
    void updateSpell_invalidInput_becomes400() throws Exception {
        SpellDocument spell = new SpellDocument();
        spell.setId("SK1");

        mockMvc.perform(put("/api/v1/spells/SK1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spell)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

        // Service is never called due to validation failure
        verifyNoInteractions(spellService);
    }

    @Test
    void deleteAllSpells_ok_returns200() throws Exception {
        when(spellService.deleteAllSpells()).thenReturn(5L);

        mockMvc.perform(delete("/api/v1/spells").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(5));

        verify(spellService).deleteAllSpells();
    }

    @Test
    void deleteAllSpells_persistenceDown_becomes503() throws Exception {
        when(spellService.deleteAllSpells()).thenThrow(new SpellPersistenceException("Failed to delete all spells"));

        mockMvc.perform(delete("/api/v1/spells").with(csrf()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(spellService).deleteAllSpells();
    }
}
