package org.springy.som.modulith.domain.item.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springy.som.modulith.exception.item.InvalidItemException;
import org.springy.som.modulith.exception.item.ItemApiExceptionHandler;
import org.springy.som.modulith.exception.item.ItemNotFoundException;
import org.springy.som.modulith.exception.item.ItemPersistenceException;

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

@WebMvcTest(ItemController.class)
@Import(ItemApiExceptionHandler.class)
@WithMockUser
public class ItemDocumentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ItemService itemService;

    @Test
    void getAllCommands_ok() throws Exception {
        when(itemService.getAllItems()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(itemService).getAllItems();
    }

    @Test
    void getCommandById_notFound_becomes404ProblemDetail() throws Exception {
        when(itemService.getItemById("A1")).thenThrow(new ItemNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/items/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(itemService).getItemById("A1");
    }

    @Test
    void getCommandById_blankId_becomes400ProblemDetail() throws Exception {
        when(itemService.getItemById(anyString())).thenThrow(new InvalidItemException("ItemDocument id must be provided"));

        mockMvc.perform(get("/api/v1/items/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(itemService).getItemById("  ");
    }


    @Test
    void getAllCommandes_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(itemService.getAllItems()).thenThrow(new ItemPersistenceException("Failed to load items"));

        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(itemService).getAllItems();
    }

    @Test
    void deleteItem_ok_returns204() throws Exception {
        doNothing().when(itemService).deleteItemById("A1");

        mockMvc.perform(delete("/api/v1/items/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(itemService).deleteItemById("A1");
    }

    @Test
    void createItem_returns201() throws Exception {
        ItemDocument input = new ItemDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("Midgaard");

        ItemDocument saved = new ItemDocument();
        saved.setAreaId("A1");
        saved.setId("I1");
        saved.setName("Midgaard");

        when(itemService.createItem(any(ItemDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/items")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.areaId").value("A1"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("Midgaard"));

        verify(itemService).createItem(any(ItemDocument.class));
    }

    @Test
    void updateItem_ok_returns200AndBody() throws Exception {
        ItemDocument input = new ItemDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("Midgaard");

        ItemDocument saved = new ItemDocument();
        saved.setAreaId("A1");
        saved.setId("I1");
        saved.setName("Midgaard (updated)");

        when(itemService.saveItemForId(eq("A1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/items/{id}", "A1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.areaId").value("A1"))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("Midgaard (updated)"));

        verify(itemService).saveItemForId(eq("A1"), eq(input));
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem_blankName_returns400ProblemDetail_asJson() throws Exception {
        ItemDocument input = new ItemDocument();
        input.setAreaId("A1");
        input.setId("I1");
        input.setName("");

        mockMvc.perform(put("/api/v1/items/{id}", "A1")
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
        when(itemService.deleteAllItems()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/items")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(itemService.deleteAllItems()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/items")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
