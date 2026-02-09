package org.springy.som.modulith.domain.command.internal;

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

@WebMvcTest(CommandController.class)
@Import(CommandApiExceptionHandler.class)
@WithMockUser
public class CommandControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    CommandService commandService;

    @Test
    void getAllCommands_ok() throws Exception {
        when(commandService.getAllCommands()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/commands"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(commandService).getAllCommands();
    }

    @Test
    void getCommandById_notFound_becomes404ProblemDetail() throws Exception {
        when(commandService.getCommandById("A1")).thenThrow(new CommandNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/commands/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(commandService).getCommandById("A1");
    }

    @Test
    void getCommandById_blankId_becomes400ProblemDetail() throws Exception {
        when(commandService.getCommandById(anyString())).thenThrow(new InvalidCommandException("CommandDocument id must be provided"));

        mockMvc.perform(get("/api/v1/commands/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(commandService).getCommandById("  ");
    }


    @Test
    void getAllCommandes_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(commandService.getAllCommands()).thenThrow(new CommandPersistenceException("Failed to load commands"));

        mockMvc.perform(get("/api/v1/commands"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(commandService).getAllCommands();
    }

    @Test
    void deleteCommand_ok_returns204() throws Exception {
        doNothing().when(commandService).deleteCommandById("A1");

        mockMvc.perform(delete("/api/v1/commands/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(commandService).deleteCommandById("A1");
    }

    @Test
    void createCommand_returns201() throws Exception {
        CommandDocument input = new CommandDocument();
        input.setName("Midgaard");

        CommandDocument saved = new CommandDocument();
        saved.setId("A1");
        saved.setName("Midgaard");

        when(commandService.createCommand(any(CommandDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/commands")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(commandService).createCommand(any(CommandDocument.class));
    }

    @Test
    void updateCommand_ok_returns200AndBody() throws Exception {
        CommandDocument input = new CommandDocument();
        input.setId("A1");
        input.setName("Midgaard");

        CommandDocument saved = new CommandDocument();
        saved.setId("A1");
        saved.setName("Midgaard (updated)");

        when(commandService.saveCommandForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/commands/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(commandService).saveCommandForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(commandService);
    }

    @Test
    void updateCommand_blankName_returns400ProblemDetail_asJson() throws Exception {
        CommandDocument input = new CommandDocument();
        input.setId("A1");
        input.setName("");

        mockMvc.perform(put("/api/v1/commands/{id}", "A1")
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
        when(commandService.deleteAllCommands()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/commands")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(commandService.deleteAllCommands()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/commands")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
