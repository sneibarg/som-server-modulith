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

@WebMvcTest(SocialController.class)
@Import(SocialApiExceptionHandler.class)
@WithMockUser
public class SocialControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SocialService socialService;

    @Mock
    SocialDocument socialDocument;

    @Test
    void getAllSocials_ok() throws Exception {
        when(socialService.getAllSocials()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/socials"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(socialService).getAllSocials();
    }

    @Test
    void getSocialById_notFound_becomes404ProblemDetail() throws Exception {
        when(socialService.getSocialById("S1")).thenThrow(new SocialNotFoundException("S1"));

        mockMvc.perform(get("/api/v1/socials/S1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(socialService).getSocialById("S1");
    }

    @Test
    void getSocialById_blankId_becomes400ProblemDetail() throws Exception {
        when(socialService.getSocialById(anyString())).thenThrow(new InvalidSocialException("SocialDocument id must be provided"));

        mockMvc.perform(get("/api/v1/socials/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(socialService).getSocialById("  ");
    }


    @Test
    void getAllSociales_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(socialService.getAllSocials()).thenThrow(new SocialPersistenceException("Failed to load commands"));

        mockMvc.perform(get("/api/v1/socials"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(socialService).getAllSocials();
    }

    @Test
    void deleteSocial_ok_returns204() throws Exception {
        doNothing().when(socialService).deleteSocialById("S1");

        mockMvc.perform(delete("/api/v1/socials/S1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(socialService).deleteSocialById("S1");
    }

    @Test
    void createSocial_returns201() throws Exception {
        SocialDocument input = new SocialDocument();
        input.setId("S1");
        input.setName("laugh");

        SocialDocument saved = new SocialDocument();
        saved.setId("S1");
        saved.setName("laugh");

        when(socialService.createSocial(any(SocialDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/socials")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("S1"))
                .andExpect(jsonPath("$.name").value("laugh"));

        verify(socialService).createSocial(any(SocialDocument.class));
    }

    @Test
    void updateSocial_ok_returns200AndBody() throws Exception {
        SocialDocument input = new SocialDocument();
        input.setId("S1");
        input.setName("laugh");

        SocialDocument saved = new SocialDocument();
        saved.setId("S1");
        saved.setName("laff");

        when(socialService.saveSocialForId(eq("S1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/socials/{id}", "S1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("S1"))
                .andExpect(jsonPath("$.name").value("laff"));

        verify(socialService).saveSocialForId(eq("S1"), eq(input));
        verifyNoMoreInteractions(socialService);
    }

    @Test
    void updateSocial_blankName_returns400ProblemDetail_asJson() throws Exception {
        SocialDocument input = new SocialDocument();
        input.setId("S1");
        input.setName("");

        mockMvc.perform(put("/api/v1/socials/{id}", "S1")
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
        when(socialService.deleteAllSocials()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/socials")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(socialService.deleteAllSocials()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/socials")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
