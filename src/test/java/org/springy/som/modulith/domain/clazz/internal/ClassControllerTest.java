package org.springy.som.modulith.domain.clazz.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassController.class)
@Import(ClassApiExceptionHandler.class)
@WithMockUser
class ClassControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ClassService classService;

    @Test
    void getAllRomClasses_ok() throws Exception {
        when(classService.getAllClasses()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/classes"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(classService).getAllClasses();
    }

    @Test
    void getRomClassById_notFound_becomes404ProblemDetail() throws Exception {
        when(classService.getRomClassById("A1")).thenThrow(new ClassNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/classes/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(classService).getRomClassById("A1");
    }

    @Test
    void getRomClassById_blankId_becomes400ProblemDetail() throws Exception {
        when(classService.getRomClassById(anyString())).thenThrow(new InvalidClassException("ROM class id must be provided"));

        mockMvc.perform(get("/api/v1/classes/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(classService).getRomClassById("  ");
    }


    @Test
    void getAllRomClasses_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(classService.getAllClasses()).thenThrow(new ClassPersistenceException("Failed to load ROM classes"));

        mockMvc.perform(get("/api/v1/classes"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(classService).getAllClasses();
    }

    @Test
    void deleteRomClass_ok_returns204() throws Exception {
        doNothing().when(classService).deleteRomClassById("A1");

        mockMvc.perform(delete("/api/v1/classes/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(classService).deleteRomClassById("A1");
    }

    @Test
    void createRomClass_returns201() throws Exception {
        ClassDocument input = new ClassDocument();
        input.setName("Midgaard");

        ClassDocument saved = new ClassDocument();
        saved.setId("A1");
        saved.setName("Midgaard");

        when(classService.createRomClass(any(ClassDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/classes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(classService).createRomClass(any(ClassDocument.class));
    }

    @Test
    void updateRomClass_ok_returns200AndBody() throws Exception {
        ClassDocument input = new ClassDocument();
        input.setId("A1");
        input.setName("Midgaard");

        ClassDocument saved = new ClassDocument();
        saved.setId("A1");
        saved.setName("Midgaard (updated)");

        when(classService.saveRomClassForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/classes/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(classService).saveRomClassForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(classService);
    }

    @Test
    void updateRomClass_blankName_returns400ProblemDetail_asJson() throws Exception {
        ClassDocument input = new ClassDocument();
        input.setId("A1");
        input.setName("");

        mockMvc.perform(put("/api/v1/classes/{id}", "A1")
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
        when(classService.deleteAllRomClasses()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/classes")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(classService.deleteAllRomClasses()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/classes")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
