package org.springy.som.modulith.domain.special.internal;

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

@WebMvcTest(SpecialController.class)
@Import(SpecialApiExceptionHandler.class)
@WithMockUser
public class SpecialControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SpecialService specialService;

    @Test
    void getAllSpecials_ok() throws Exception {
        when(specialService.getAllSpecials()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/specials"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(specialService).getAllSpecials();
    }

    @Test
    void getShopById_notFound_becomes404ProblemDetail() throws Exception {
        when(specialService.getSpecialById("A1")).thenThrow(new SpecialNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/specials/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(specialService).getSpecialById("A1");
    }

    @Test
    void getShopById_blankId_becomes400ProblemDetail() throws Exception {
        when(specialService.getSpecialById(anyString())).thenThrow(new InvalidSpecialException("Player id must be provided"));

        mockMvc.perform(get("/api/v1/specials/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(specialService).getSpecialById("  ");
    }


    @Test
    void getAllSpecial_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(specialService.getAllSpecials()).thenThrow(new SpecialPersistenceException("Failed to load players"));

        mockMvc.perform(get("/api/v1/specials"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(specialService).getAllSpecials();
    }

    @Test
    void deleteSpecial_ok_returns204() throws Exception {
        doNothing().when(specialService).deleteSpecialById("A1");

        mockMvc.perform(delete("/api/v1/specials/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(specialService).deleteSpecialById("A1");
    }

    @Test
    void createSpecial_returns201() throws Exception {
        SpecialDocument input = new SpecialDocument();
        input.setId("I1");
        input.setAreaId("A1");

        SpecialDocument saved = new SpecialDocument();
        saved.setId("I1");
        saved.setAreaId("A1");

        when(specialService.createSpecial(any(SpecialDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/specials")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(specialService).createSpecial(any(SpecialDocument.class));
    }

    @Test
    void updateSpecial_ok_returns200AndBody() throws Exception {
        SpecialDocument input = new SpecialDocument();
        input.setId("I1");
        input.setAreaId("A1");

        SpecialDocument saved = new SpecialDocument();
        saved.setId("I1");
        saved.setAreaId("A1");

        when(specialService.saveSpecialForId(eq("I1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/specials/{id}", "I1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(specialService).saveSpecialForId(eq("I1"), eq(input));
        verifyNoMoreInteractions(specialService);
    }

    @Test
    void updateSpecial_blankName_returns400ProblemDetail_asJson() throws Exception {
        SpecialDocument input = new SpecialDocument();
        input.setId("I1");

        mockMvc.perform(put("/api/v1/specials/{id}", "I1")
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
        when(specialService.deleteAllSpecials()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/specials")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(specialService.deleteAllSpecials()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/specials")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
