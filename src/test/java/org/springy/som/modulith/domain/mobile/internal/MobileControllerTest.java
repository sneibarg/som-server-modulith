package org.springy.som.modulith.domain.mobile.internal;

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

@WebMvcTest(MobileController.class)
@Import(MobileApiExceptionHandler.class)
@WithMockUser
public class MobileControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MobileService mobileService;

    @Test
    void getAllMobiles_ok() throws Exception {
        when(mobileService.getAllMobiles()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/mobiles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(mobileService).getAllMobiles();
    }

    @Test
    void getMobileById_notFound_becomes404ProblemDetail() throws Exception {
        when(mobileService.getMobileById("A1")).thenThrow(new MobileNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/mobiles/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(mobileService).getMobileById("A1");
    }

    @Test
    void getMobileById_blankId_becomes400ProblemDetail() throws Exception {
        when(mobileService.getMobileById(anyString())).thenThrow(new InvalidMobileException("ItemDocument id must be provided"));

        mockMvc.perform(get("/api/v1/mobiles/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(mobileService).getMobileById("  ");
    }


    @Test
    void getAllMobiles_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(mobileService.getAllMobiles()).thenThrow(new MobilePersistenceException("Failed to load items"));

        mockMvc.perform(get("/api/v1/mobiles"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(mobileService).getAllMobiles();
    }

    @Test
    void deleteMobile_ok_returns204() throws Exception {
        doNothing().when(mobileService).deleteMobileById("A1");

        mockMvc.perform(delete("/api/v1/mobiles/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(mobileService).deleteMobileById("A1");
    }

    @Test
    void createMobile_returns201() throws Exception {
        MobileDocument input = new MobileDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("Midgaard");

        MobileDocument saved = new MobileDocument();
        saved.setAreaId("A1");
        saved.setId("I1");
        saved.setName("Midgaard");

        when(mobileService.createMobile(any(MobileDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/mobiles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.areaId").value("A1"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(mobileService).createMobile(any(MobileDocument.class));
    }

    @Test
    void updateMobile_ok_returns200AndBody() throws Exception {
        MobileDocument input = new MobileDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("Midgaard");

        MobileDocument saved = new MobileDocument();
        saved.setAreaId("A1");
        saved.setId("I1");
        saved.setName("Midgaard (updated)");

        when(mobileService.saveMobileForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/mobiles/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.areaId").value("A1"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(mobileService).saveMobileForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(mobileService);
    }

    @Test
    void updateMobile_blankName_returns400ProblemDetail_asJson() throws Exception {
        MobileDocument input = new MobileDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("");

        mockMvc.perform(put("/api/v1/mobiles/{id}", "A1")
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
        when(mobileService.deleteAllMobiles()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/mobiles")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(mobileService.deleteAllMobiles()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/mobiles")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
