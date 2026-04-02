package org.springy.som.modulith.domain.note.internal;

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

@WebMvcTest(NoteController.class)
@Import(NoteApiExceptionHandler.class)
@WithMockUser
public class NoteControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    NoteService noteService;

    @Mock
    NoteDocument noteDocument;

    @Test
    void getAllNotes_ok() throws Exception {
        when(noteService.getAllNotes()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/notes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(noteService).getAllNotes();
    }

    @Test
    void getNoteById_notFound_becomes404ProblemDetail() throws Exception {
        when(noteService.getNoteById("N1")).thenThrow(new NoteNotFoundException("N1"));

        mockMvc.perform(get("/api/v1/notes/N1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(noteService).getNoteById("N1");
    }

    @Test
    void getNoteById_blankId_becomes400ProblemDetail() throws Exception {
        when(noteService.getNoteById(anyString())).thenThrow(new InvalidNoteException("NoteDocument id must be provided"));

        mockMvc.perform(get("/api/v1/notes/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(noteService).getNoteById("  ");
    }


    @Test
    void getAllNotes_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(noteService.getAllNotes()).thenThrow(new NotePersistenceException("Failed to load notes"));

        mockMvc.perform(get("/api/v1/notes"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(noteService).getAllNotes();
    }

    @Test
    void deleteNote_ok_returns204() throws Exception {
        doNothing().when(noteService).deleteNoteById("N1");

        mockMvc.perform(delete("/api/v1/notes/N1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(noteService).deleteNoteById("N1");
    }

    @Test
    void createNote_returns201() throws Exception {
        NoteDocument input = new NoteDocument();
        input.setId("N1");
        input.setType(0);
        input.setSender("admin");
        input.setSubject("Test Note");
        input.setText("Note text");
        input.setDateStamp(1234567890L);

        NoteDocument saved = new NoteDocument();
        saved.setId("N1");
        saved.setType(0);
        saved.setSender("admin");
        saved.setSubject("Test Note");
        saved.setText("Note text");
        saved.setDateStamp(1234567890L);

        when(noteService.createNote(any(NoteDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/notes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("N1"))
                .andExpect(jsonPath("$.sender").value("admin"))
                .andExpect(jsonPath("$.subject").value("Test Note"));

        verify(noteService).createNote(any(NoteDocument.class));
    }

    @Test
    void updateNote_ok_returns200AndBody() throws Exception {
        NoteDocument input = new NoteDocument();
        input.setId("N1");
        input.setType(0);
        input.setSender("admin");
        input.setSubject("Test Note");
        input.setText("Note text");

        NoteDocument saved = new NoteDocument();
        saved.setId("N1");
        saved.setType(1);
        saved.setSender("admin");
        saved.setSubject("Updated Note");
        saved.setText("Updated text");

        when(noteService.saveNoteForId(eq("N1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/notes/{id}", "N1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("N1"))
                .andExpect(jsonPath("$.sender").value("admin"))
                .andExpect(jsonPath("$.subject").value("Updated Note"))
                .andExpect(jsonPath("$.type").value(1));

        verify(noteService).saveNoteForId(eq("N1"), eq(input));
        verifyNoMoreInteractions(noteService);
    }

    @Test
    void updateNote_blankSender_returns400ProblemDetail_asJson() throws Exception {
        NoteDocument input = new NoteDocument();
        input.setId("N1");
        input.setSender("");
        input.setSubject("Test");
        input.setText("text");

        mockMvc.perform(put("/api/v1/notes/{id}", "N1")
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
        when(noteService.deleteAllNotes()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/notes")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(noteService.deleteAllNotes()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/notes")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }

    @Test
    void getNotesByType_ok() throws Exception {
        when(noteService.getNotesByType(0)).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/notes/type/0"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(noteService).getNotesByType(0);
    }
}
