package org.springy.som.modulith.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springy.som.modulith.domain.character.PlayerCharacter;
import org.springy.som.modulith.exception.character.InvalidPlayerCharacterException;
import org.springy.som.modulith.exception.character.PlayerCharacterApiExceptionHandler;
import org.springy.som.modulith.exception.character.PlayerCharacterNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springy.som.modulith.exception.character.PlayerCharacterPersistenceException;
import org.springy.som.modulith.service.CharacterService;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CharacterController.class)
@Import(PlayerCharacterApiExceptionHandler.class)
@WithMockUser
class PlayerCharacterControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CharacterService characterService;

    @Test
    void getAllPlayerCharacters_ok() throws Exception {
        when(characterService.getAllPlayerCharacters()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/characters"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(characterService).getAllPlayerCharacters();
    }

    @Test
    void getPlayerCharacterById_notFound_becomes404ProblemDetail() throws Exception {
        when(characterService.getPlayerCharacterById("A1")).thenThrow(new PlayerCharacterNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/characters/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(characterService).getPlayerCharacterById("A1");
    }

    @Test
    void getPlayerCharacterById_blankId_becomes400ProblemDetail() throws Exception {
        when(characterService.getPlayerCharacterById(anyString())).thenThrow(new InvalidPlayerCharacterException("Player Character id must be provided"));

        mockMvc.perform(get("/api/v1/characters/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(characterService).getPlayerCharacterById("  ");
    }


    @Test
    void getAllPlayerCharacters_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(characterService.getAllPlayerCharacters()).thenThrow(new PlayerCharacterPersistenceException("Failed to load player characters"));

        mockMvc.perform(get("/api/v1/characters"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(characterService).getAllPlayerCharacters();
    }

    @Test
    void deletePlayerCharacter_ok_returns204() throws Exception {
        doNothing().when(characterService).deletePlayerCharacterById("A1");

        mockMvc.perform(delete("/api/v1/characters/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(characterService).deletePlayerCharacterById("A1");
    }

    @Test
    void createPlayerCharacter_returns201() throws Exception {
        PlayerCharacter input = new PlayerCharacter();
        input.setName("Midgaard");

        PlayerCharacter saved = new PlayerCharacter();
        saved.setId("A1");
        saved.setName("Midgaard");

        when(characterService.savePlayerCharacter(any(PlayerCharacter.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/characters")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(characterService).savePlayerCharacter(any(PlayerCharacter.class));
    }

    @Test
    void updatePlayerCharacter_ok_returns200AndBody() throws Exception {
        PlayerCharacter input = new PlayerCharacter();
        input.setId("A1");
        input.setName("Midgaard");

        PlayerCharacter saved = new PlayerCharacter();
        saved.setId("A1");
        saved.setName("Midgaard (updated)");

        when(characterService.savePlayerCharacterForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/characters/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(characterService).savePlayerCharacterForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(characterService);
    }

    @Test
    void updatePlayerCharacter_blankName_returns400ProblemDetail_asJson() throws Exception {
        PlayerCharacter input = new PlayerCharacter();
        input.setId("A1");
        input.setName("");

        mockMvc.perform(put("/api/v1/characters/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void deleteAll_returns200AndDeletedCount() throws Exception {
        when(characterService.deleteAllPlayerCharacters()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/characters")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(characterService.deleteAllPlayerCharacters()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/characters")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
