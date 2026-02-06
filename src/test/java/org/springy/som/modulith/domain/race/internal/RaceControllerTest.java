package org.springy.som.modulith.domain.race.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(RaceController.class)
@Import(RaceApiExceptionHandler.class)
@WithMockUser
public class RaceControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RaceService raceService;

    @Test
    void getAllPlayers_ok() throws Exception {
        when(raceService.getAllRaces()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/races"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(raceService).getAllRaces();
    }

    @Test
    void getPlayerById_notFound_becomes404ProblemDetail() throws Exception {
        when(raceService.getRaceById("A1")).thenThrow(new RaceNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/races/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(raceService).getRaceById("A1");
    }

    @Test
    void getPlayerById_blankId_becomes400ProblemDetail() throws Exception {
        when(raceService.getRaceById(anyString())).thenThrow(new InvalidRomRaceException("Player id must be provided"));

        mockMvc.perform(get("/api/v1/races/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(raceService).getRaceById("  ");
    }


    @Test
    void getAllPlayer_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(raceService.getAllRaces()).thenThrow(new RacePersistenceException("Failed to load players"));

        mockMvc.perform(get("/api/v1/races"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(raceService).getAllRaces();
    }

    @Test
    void deletePlayer_ok_returns204() throws Exception {
        doNothing().when(raceService).deleteRaceById("A1");

        mockMvc.perform(delete("/api/v1/races/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(raceService).deleteRaceById("A1");
    }

    @Test
    void createPlayer_returns201() throws Exception {
        RaceDocument input = new RaceDocument();
        input.setId("I1");
        input.setName("scaryMob");
        
        RaceDocument saved = new RaceDocument();
        saved.setId("I1");
        saved.setName("scaryMob");
        
        when(raceService.createRace(any(RaceDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/races")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("scaryMob"))
                .andExpect(jsonPath("$.id").value("I1"));

        verify(raceService).createRace(any(RaceDocument.class));
    }

    @Test
    void updatePlayer_ok_returns200AndBody() throws Exception {
        RaceDocument input = new RaceDocument();
        input.setId("I1");
        input.setName("scaryMob");

        RaceDocument saved = new RaceDocument();
        saved.setId("I1");
        saved.setName("scaryMob");


        when(raceService.saveRaceForId(eq("I1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/races/{id}", "I1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("scaryMob"))
                .andExpect(jsonPath("$.id").value("I1"));

        verify(raceService).saveRaceForId(eq("I1"), eq(input));
        verifyNoMoreInteractions(raceService);
    }

    @Test
    void updatePlayer_blankName_returns400ProblemDetail_asJson() throws Exception {
        RaceDocument input = new RaceDocument();
        input.setId("I1");
        input.setName("");

        mockMvc.perform(put("/api/v1/races/{id}", "I1")
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
        when(raceService.deleteAllRaces()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/races")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(raceService.deleteAllRaces()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/races")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
