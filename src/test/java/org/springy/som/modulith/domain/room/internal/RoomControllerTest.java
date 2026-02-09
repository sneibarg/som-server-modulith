package org.springy.som.modulith.domain.room.internal;

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

@WebMvcTest(RoomController.class)
@Import(RoomApiExceptionHandler.class)
@WithMockUser
public class RoomControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    RoomService roomService;

    @Test
    void getAllResets_ok() throws Exception {
        when(roomService.getAllRooms()).thenReturn(java.util.List.of());

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(roomService).getAllRooms();
    }

    @Test
    void getResetById_notFound_becomes404ProblemDetail() throws Exception {
        when(roomService.getRoomById("A1")).thenThrow(new RoomNotFoundException("A1"));

        mockMvc.perform(get("/api/v1/rooms/A1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(roomService).getRoomById("A1");
    }

    @Test
    void getResetById_blankId_becomes400ProblemDetail() throws Exception {
        when(roomService.getRoomById(anyString())).thenThrow(new InvalidRoomException("Player id must be provided"));

        mockMvc.perform(get("/api/v1/rooms/{id}", "  ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());

        verify(roomService).getRoomById("  ");
    }


    @Test
    void getAllReset_persistenceDown_becomes503ProblemDetail() throws Exception {
        when(roomService.getAllRooms()).thenThrow(new RoomPersistenceException("Failed to load players"));

        mockMvc.perform(get("/api/v1/rooms"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(503));

        verify(roomService).getAllRooms();
    }

    @Test
    void deleteReset_ok_returns204() throws Exception {
        doNothing().when(roomService).deleteRoomById("A1");

        mockMvc.perform(delete("/api/v1/rooms/A1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(roomService).deleteRoomById("A1");
    }

    @Test
    void createReset_returns201() throws Exception {
        RoomDocument input = new RoomDocument();
        input.setId("I1");
        input.setAreaId("A1");
        input.setName("name");

        RoomDocument saved = new RoomDocument();
        saved.setId("I1");
        saved.setAreaId("A1");
        saved.setName("name");
        

        when(roomService.createRoom(any(RoomDocument.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/rooms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(roomService).createRoom(any(RoomDocument.class));
    }

    @Test
    void updateReset_ok_returns200AndBody() throws Exception {
        RoomDocument input = new RoomDocument();
        input.setId("I1");
        input.setAreaId("A1");
        input.setName("name");

        RoomDocument saved = new RoomDocument();
        saved.setId("I1");
        saved.setAreaId("A1");
        saved.setName("name");


        when(roomService.saveRoomForId(eq("I1"), eq(input))).thenReturn(saved);

        mockMvc.perform(put("/api/v1/rooms/{id}", "I1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("I1"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.areaId").value("A1"));

        verify(roomService).saveRoomForId(eq("I1"), eq(input));
        verifyNoMoreInteractions(roomService);
    }

    @Test
    void updateReset_blankName_returns400ProblemDetail_asJson() throws Exception {
        RoomDocument input = new RoomDocument();
        input.setId("I1");

        mockMvc.perform(put("/api/v1/rooms/{id}", "I1")
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
        when(roomService.deleteAllRooms()).thenReturn(7L);

        mockMvc.perform(delete("/api/v1/rooms")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(7));
    }

    @Test
    void deleteAll_whenNothingDeleted_returns200AndZero() throws Exception {
        when(roomService.deleteAllRooms()).thenReturn(0L);

        mockMvc.perform(delete("/api/v1/rooms")
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.deletedCount").value(0));
    }
}
