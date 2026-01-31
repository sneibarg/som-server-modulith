package org.springy.som.modulith.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.domain.shop.Shop;
import org.springy.som.modulith.exception.shop.InvalidShopException;
import org.springy.som.modulith.exception.shop.ShopApiExceptionHandler;
import org.springy.som.modulith.exception.shop.ShopNotFoundException;
import org.springy.som.modulith.exception.shop.ShopPersistenceException;
import org.springy.som.modulith.service.ShopService;

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

@WebMvcTest(ShopController.class)
@Import(ShopApiExceptionHandler.class)
@WithMockUser
public class ShopControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ShopService shopService;

    @Test
    void getAllShops_ok() throws Exception {
        when(shopService.getAllShops()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/shops"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(shopService).getAllShops();
    }

    @Test
    void getShopById_notFound_becomes404ProblemDetail() throws Exception {
        when(shopService.getShopById("A1")).thenThrow(new ShopNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/shops/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(shopService).getShopById("A1");
    }

    @Test
    void getShopById_blankId_becomes400ProblemDetail() throws Exception {
        when(shopService.getShopById(anyString())).thenThrow(new InvalidShopException("Player id must be provided"));

        mockMvc.perform(get("/api/v1/shops/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(shopService).getShopById("  ");
    }


    @Test
    void getAllShop_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(shopService.getAllShops()).thenThrow(new ShopPersistenceException("Failed to load players"));

        mockMvc.perform(get("/api/v1/shops"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(shopService).getAllShops();
    }

    @Test
    void deleteShop_ok_returns204() throws Exception {
        doNothing().when(shopService).deleteShopById("A1");

        mockMvc.perform(delete("/api/v1/shops/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(shopService).deleteShopById("A1");
    }

    @Test
    void createShop_returns201() throws Exception {
        Shop input = new Shop();
        input.setId("I1");
        input.setAreaId("A1");

        Shop saved = new Shop();
        saved.setId("I1");
        saved.setAreaId("A1");

        when(shopService.createShop(any(Shop.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/shops")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(shopService).createShop(any(Shop.class));
    }

    @Test
    void updateShop_ok_returns200AndBody() throws Exception {
        Shop input = new Shop();
        input.setId("I1");
        input.setAreaId("A1");

        Shop saved = new Shop();
        saved.setId("I1");
        saved.setAreaId("A1");

        when(shopService.saveShopForId(eq("I1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/shops/{id}", "I1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(shopService).saveShopForId(eq("I1"), eq(input));
        verifyNoMoreInteractions(shopService);
    }

    @Test
    void updateShop_blankName_returns400ProblemDetail_asJson() throws Exception {
        Shop input = new Shop();
        input.setId("I1");

        mockMvc.perform(put("/api/v1/shops/{id}", "I1")
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
        when(shopService.deleteAllShops()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/shops")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(shopService.deleteAllShops()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/shops")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
