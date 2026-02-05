package org.springy.som.modulith.domain.area.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springy.som.modulith.exception.area.AreaApiExceptionHandler;
import org.springy.som.modulith.exception.area.AreaNotFoundException;
import org.springy.som.modulith.exception.area.AreaPersistenceException;
import org.springy.som.modulith.exception.area.InvalidAreaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AreaController.class)
@Import(AreaApiExceptionHandler.class)
@WithMockUser
class AreaDocumentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AreaService areaService;

    @MockitoBean
    AreaRepository areaRepository;

    @Mock
    AreaDocument areaDocument;

    @Test
    void getAllAreas_ok() throws Exception {
        when(areaService.getAllAreas()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/areas"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(areaService).getAllAreas();
    }

    @Test
    void getAreaById_notFound_becomes404ProblemDetail() throws Exception {
        when(areaService.getAreaById("A1")).thenThrow(new AreaNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/areas/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(areaService).getAreaById("A1");
    }

    @Test
    void getAreaById_blankId_becomes400ProblemDetail() throws Exception {
        when(areaService.getAreaById(anyString())).thenThrow(new InvalidAreaException("AreaDocument id must be provided"));
        when(areaRepository.findAreaByAreaId(anyString())).thenReturn(areaDocument);

        mockMvc.perform(get("/api/v1/areas/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(areaService).getAreaById("  ");
    }


    @Test
    void getAllAreas_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(areaService.getAllAreas()).thenThrow(new AreaPersistenceException("Failed to load areas"));

        mockMvc.perform(get("/api/v1/areas"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(areaService).getAllAreas();
    }

    @Test
    void deleteArea_ok_returns204() throws Exception {
        doNothing().when(areaService).deleteAreaById("A1");

        mockMvc.perform(delete("/api/v1/areas/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(areaService).deleteAreaById("A1");
    }

    @Test
    void createArea_returns201() throws Exception {
        AreaDocument input = new AreaDocument();
        input.setName("Midgaard");

        AreaDocument saved = new AreaDocument();
        saved.setId("A1");
        saved.setName("Midgaard");

        when(areaService.createArea(any(AreaDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/areas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(areaService).createArea(any(AreaDocument.class));
    }

    @Test
    void updateArea_ok_returns200AndBody() throws Exception {
        AreaDocument input = new AreaDocument();
        input.setId("A1");
        input.setName("Midgaard");

        AreaDocument saved = new AreaDocument();
        saved.setId("A1");
        saved.setName("Midgaard (updated)");

        when(areaService.saveAreaForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/areas/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("A1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(areaService).saveAreaForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(areaService);
    }

    @Test
    void updateArea_blankName_returns400ProblemDetail_asJson() throws Exception {
        AreaDocument input = new AreaDocument();
        input.setId("A1");
        input.setName("");

        mockMvc.perform(put("/api/v1/areas/{id}", "A1")
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
        when(areaService.deleteAllAreas()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/areas")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(areaService.deleteAllAreas()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/areas")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
