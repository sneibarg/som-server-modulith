package org.springy.som.modulith.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springy.som.modulith.domain.player.PlayerAccount;
import org.springy.som.modulith.exception.player.InvalidPlayerException;
import org.springy.som.modulith.exception.player.PlayerApiExceptionHandler;
import org.springy.som.modulith.exception.player.PlayerNotFoundException;
import org.springy.som.modulith.exception.player.PlayerPersistenceException;
import org.springy.som.modulith.service.PlayerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
@Import(PlayerApiExceptionHandler.class)
@WithMockUser
public class PlayerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    PlayerService playerService;

    @Test
    void getAllPlayers_ok() throws Exception {
        when(playerService.getAllPlayerAccounts()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(playerService).getAllPlayerAccounts();
    }

    @Test
    void getPlayerById_notFound_becomes404ProblemDetail() throws Exception {
        when(playerService.getPlayerAccountById("A1")).thenThrow(new PlayerNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/players/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(playerService).getPlayerAccountById("A1");
    }

    @Test
    void getPlayerById_blankId_becomes400ProblemDetail() throws Exception {
        when(playerService.getPlayerAccountById(anyString())).thenThrow(new InvalidPlayerException("Player id must be provided"));

        mockMvc.perform(get("/api/v1/players/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(playerService).getPlayerAccountById("  ");
    }


    @Test
    void getAllPlayer_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(playerService.getAllPlayerAccounts()).thenThrow(new PlayerPersistenceException("Failed to load players"));

        mockMvc.perform(get("/api/v1/players"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(playerService).getAllPlayerAccounts();
    }

    @Test
    void deletePlayer_ok_returns204() throws Exception {
        doNothing().when(playerService).deletePlayerAccountById("A1");

        mockMvc.perform(delete("/api/v1/players/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(playerService).deletePlayerAccountById("A1");
    }

    @Test
    void createPlayer_returns201() throws Exception {
        PlayerAccount input = new PlayerAccount();
        input.setId("I1");
        input.setAccountName("happyPlayer");
        input.setFirstName("Happy");
        input.setLastName("Player");
        input.setPassword("happyPlayer123");
        input.setEmailAddress("happyPlayer@funtimes.net");

        PlayerAccount saved = new PlayerAccount();
        saved.setId("I1");
        saved.setAccountName("happyPlayer");
        saved.setFirstName("Happy");
        saved.setLastName("Player");
        saved.setPassword("happyPlayer123");
        saved.setEmailAddress("happyPlayer@funtimes.net");

        when(playerService.createPlayerAccount(any(PlayerAccount.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/players")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountName").value("happyPlayer"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.firstName").value("Happy"))
                .andExpect(jsonPath("$.lastName").value("Player"))
                .andExpect(jsonPath("$.password").value("happyPlayer123"))
                .andExpect(jsonPath("$.emailAddress").value("happyPlayer@funtimes.net"));

        verify(playerService).createPlayerAccount(any(PlayerAccount.class));
    }

    @Test
    void updatePlayer_ok_returns200AndBody() throws Exception {
        PlayerAccount input = new PlayerAccount();
        input.setId("I1");
        input.setAccountName("happyPlayer");
        input.setFirstName("Happy");
        input.setLastName("Player");
        input.setPassword("happyPlayer123");
        input.setEmailAddress("happyPlayer@funtimes.net");

        PlayerAccount saved = new PlayerAccount();
        saved.setId("I1");
        saved.setAccountName("happyPlayer");
        saved.setFirstName("Happy");
        saved.setLastName("Player");
        saved.setPassword("happyPlayer123");
        saved.setEmailAddress("happyPlayer@funtimes.net");

        when(playerService.savePlayerAccountForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/players/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountName").value("happyPlayer"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.firstName").value("Happy"))
                .andExpect(jsonPath("$.lastName").value("Player"))
                .andExpect(jsonPath("$.password").value("happyPlayer123"))
                .andExpect(jsonPath("$.emailAddress").value("happyPlayer@funtimes.net"));
        
        verify(playerService).savePlayerAccountForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(playerService);
    }

    @Test
    void updatePlayer_blankAccountName_returns400ProblemDetail_asJson() throws Exception {
        PlayerAccount input = new PlayerAccount();
        input.setId("I1");
        input.setAccountName("");
        input.setFirstName("Happy");
        input.setLastName("Player");
        input.setPassword("happyPlayer123");
        input.setEmailAddress("happyPlayer@funtimes.net");

        mockMvc.perform(put("/api/v1/players/{id}", "I1")
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
        when(playerService.deleteAllPlayerAccounts()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/players")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(playerService.deleteAllPlayerAccounts()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/players")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
