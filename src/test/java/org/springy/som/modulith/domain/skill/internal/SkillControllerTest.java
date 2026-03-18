package org.springy.som.modulith.domain.skill.internal;

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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@Import(SkillApiExceptionHandler.class)
@WithMockUser
public class SkillControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SkillService skillService;

    @MockitoBean
    SkillRepository skillRepository;

    @Mock
    SkillDocument skillDocument;

    @Test
    void getAllSkills_ok() throws Exception {
        when(skillService.getAllSkills()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(skillService).getAllSkills();
    }

    @Test
    void getSkillById_notFound_becomes404ProblemDetail() throws Exception {
        when(skillService.getSkillById("SK1")).thenThrow(new SkillNotFoundException("SK1"));

        mockMvc.perform(get("/api/v1/skills/SK1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(skillService).getSkillById("SK1");
    }

    @Test
    void getSkillById_blankId_becomes400ProblemDetail() throws Exception {
        when(skillService.getSkillById(anyString())).thenThrow(new InvalidSkillException("ROM skill id must be provided"));
        when(skillRepository.findSkillById(anyString())).thenReturn(skillDocument);

        mockMvc.perform(get("/api/v1/skills/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(skillService).getSkillById("  ");
    }

    @Test
    void getAllSkills_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(skillService.getAllSkills()).thenThrow(new SkillPersistenceException("Failed to load skills"));

        mockMvc.perform(get("/api/v1/skills"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(skillService).getAllSkills();
    }

    @Test
    void deleteSkill_ok_returns204() throws Exception {
        doNothing().when(skillService).deleteSkillById("SK1");

        mockMvc.perform(delete("/api/v1/skills/SK1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(skillService).deleteSkillById("SK1");
    }

    @Test
    void deleteSkill_notFound_becomes404() throws Exception {
        when(skillService.getSkillById("SK1")).thenThrow(new SkillNotFoundException("SK1"));

        mockMvc.perform(get("/api/v1/skills/SK1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));

        verify(skillService).getSkillById("SK1");
    }

    @Test
    void createSkill_ok_returns201() throws Exception {
        SkillDocument skill = new SkillDocument();
        skill.setName("Fireball");

        SkillDocument saved = new SkillDocument();
        saved.setId("SK1");
        saved.setName("Fireball");

        when(skillService.createSkill(any(SkillDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/skills")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("SK1"))
                .andExpect(jsonPath("$.name").value("Fireball"));

        verify(skillService).createSkill(any(SkillDocument.class));
    }

    @Test
    void createSkill_invalidInput_becomes400() throws Exception {
        SkillDocument skill =  new SkillDocument();
        skill.setId("SK1");
        when(skillService.createSkill(any(SkillDocument.class))).thenThrow(new InvalidSkillException("ROM skill id must be provided"));

        mockMvc.perform(post("/api/v1/skills")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void updateSkill_ok_returns200() throws Exception {
        SkillDocument skill = new SkillDocument();
        skill.setId("SK1");
        skill.setName("Updated Fireball");

        when(skillService.saveSkillForId(eq("SK1"), any(SkillDocument.class))).thenReturn(skill);

        mockMvc.perform(put("/api/v1/skills/SK1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("SK1"))
                .andExpect(jsonPath("$.name").value("Updated Fireball"));

        verify(skillService).saveSkillForId(eq("SK1"), any(SkillDocument.class));
    }

    @Test
    void updateSkill_invalidInput_becomes400() throws Exception {
        SkillDocument skill = new SkillDocument();
        skill.setId("SK1");

        mockMvc.perform(put("/api/v1/skills/SK1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400));

        // Service is never called due to validation failure
        verifyNoInteractions(skillService);
    }

    @Test
    void deleteAllSkills_ok_returns200() throws Exception {
        when(skillService.deleteAllSkills()).thenReturn(5L);

        mockMvc.perform(delete("/api/v1/skills").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(5));

        verify(skillService).deleteAllSkills();
    }

    @Test
    void deleteAllSkills_persistenceDown_becomes503() throws Exception {
        when(skillService.deleteAllSkills()).thenThrow(new SkillPersistenceException("Failed to delete all skills"));

        mockMvc.perform(delete("/api/v1/skills").with(csrf()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(skillService).deleteAllSkills();
    }
}
