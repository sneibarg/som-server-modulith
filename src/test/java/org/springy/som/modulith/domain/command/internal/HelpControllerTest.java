package org.springy.som.modulith.domain.command.internal;

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

@WebMvcTest(HelpController.class)
@Import(HelpApiExceptionHandler.class)
@WithMockUser
public class HelpControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    HelpService helpService;

    @Mock
    HelpDocument helpDocument;

    @Test
    void getAllHelps_ok() throws Exception {
        when(helpService.getAllHelps()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/helps"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(helpService).getAllHelps();
    }

    @Test
    void getHelpById_notFound_becomes404ProblemDetail() throws Exception {
        when(helpService.getHelpById("H1")).thenThrow(new HelpNotFoundException("H1"));

        mockMvc.perform(get("/api/v1/helps/H1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(helpService).getHelpById("H1");
    }

    @Test
    void getHelpById_blankId_becomes400ProblemDetail() throws Exception {
        when(helpService.getHelpById(anyString())).thenThrow(new InvalidHelpException("HelpDocument id must be provided"));

        mockMvc.perform(get("/api/v1/helps/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(helpService).getHelpById("  ");
    }


    @Test
    void getAllHelps_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(helpService.getAllHelps()).thenThrow(new HelpPersistenceException("Failed to load helps"));

        mockMvc.perform(get("/api/v1/helps"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(helpService).getAllHelps();
    }

    @Test
    void deleteHelp_ok_returns204() throws Exception {
        doNothing().when(helpService).deleteHelpById("H1");

        mockMvc.perform(delete("/api/v1/helps/H1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(helpService).deleteHelpById("H1");
    }

    @Test
    void createHelp_returns201() throws Exception {
        HelpDocument input = new HelpDocument();
        input.setId("H1");
        input.setKeyword("combat");
        input.setLevel(0);
        input.setText("Combat help text");

        HelpDocument saved = new HelpDocument();
        saved.setId("H1");
        saved.setKeyword("combat");
        saved.setLevel(0);
        saved.setText("Combat help text");

        when(helpService.createHelp(any(HelpDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/helps")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("H1"))
                .andExpect(jsonPath("$.keyword").value("combat"));

        verify(helpService).createHelp(any(HelpDocument.class));
    }

    @Test
    void updateHelp_ok_returns200AndBody() throws Exception {
        HelpDocument input = new HelpDocument();
        input.setId("H1");
        input.setKeyword("combat");
        input.setLevel(0);
        input.setText("Combat help text");

        HelpDocument saved = new HelpDocument();
        saved.setId("H1");
        saved.setKeyword("combat");
        saved.setLevel(1);
        saved.setText("Updated combat help text");

        when(helpService.saveHelpForId(eq("H1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/helps/{id}", "H1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("H1"))
                .andExpect(jsonPath("$.keyword").value("combat"))
                .andExpect(jsonPath("$.level").value(1));

        verify(helpService).saveHelpForId(eq("H1"), eq(input));
        verifyNoMoreInteractions(helpService);
    }

    @Test
    void updateHelp_blankKeyword_returns400ProblemDetail_asJson() throws Exception {
        HelpDocument input = new HelpDocument();
        input.setId("H1");
        input.setKeyword("");
        input.setLevel(0);
        input.setText("text");

        mockMvc.perform(put("/api/v1/helps/{id}", "H1")
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
        when(helpService.deleteAllHelps()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/helps")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(helpService.deleteAllHelps()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/helps")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
