package org.springy.som.modulith.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.domain.room.Room;
import org.springy.som.modulith.exception.room.InvalidRoomException;
import org.springy.som.modulith.exception.room.RoomNotFoundException;
import org.springy.som.modulith.exception.room.RoomPersistenceException;
import org.springy.som.modulith.repository.RoomRepository;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock RoomRepository repo;

    private RoomService service;

    @BeforeEach
    void setUp() {
        service = new RoomService(repo);
    }

    @Test
    void getAllRooms_ok() {
        List<Room> rooms = List.of(mock(Room.class), mock(Room.class));
        when(repo.findAll()).thenReturn(rooms);

        assertThat(service.getAllRooms()).isSameAs(rooms);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRoomByName_delegates() {
        Room room = mock(Room.class);
        when(repo.findRoomById("Midgaard")).thenReturn(room);

        assertThat(service.getRoomByName("Midgaard")).isSameAs(room);

        verify(repo).findRoomById("Midgaard");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRoomById_delegates() {
        Room room = mock(Room.class);
        when(repo.findRoomById("R1")).thenReturn(room);

        assertThat(service.getRoomById("R1")).isSameAs(room);

        verify(repo).findRoomById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_null_becomesInvalidRoomException() {
        assertThatThrownBy(() -> service.createRoom(null))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining("ROM room must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createRoom_blankId_becomesInvalidRoomException() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createRoom(room))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining("ROM room id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createRoom_ok_saves() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn("R1");
        when(repo.save(room)).thenReturn(room);

        assertThat(service.createRoom(room)).isSameAs(room);

        verify(repo).save(room);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_dataAccess_becomesRoomPersistenceException() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn("R1");
        when(repo.save(room)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRoom(room))
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(room);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_dataAccess_safeIdExceptionPath_becomesRoomPersistenceException() {
        Room room = mock(Room.class);
        when(room.getId()).thenReturn("R1").thenThrow(new RuntimeException("boom"));
        when(repo.save(room)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRoom(room))
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining("Failed to create ROM race");

        verify(repo).save(room);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveRoomForId_blankId_becomesInvalidRoomException() {
        Room room = mock(Room.class);

        assertThatThrownBy(() -> service.saveRoomForId(" ", room))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining("ROM room id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveRoomForId_nullRoom_becomesInvalidRoomException() {
        assertThatThrownBy(() -> service.saveRoomForId("R1", null))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining("ROM room must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveRoomForId_ok_savesByLookup() {
        Room input = mock(Room.class);
        when(input.getId()).thenReturn("R1");

        Room existing = mock(Room.class);
        when(repo.findRoomById("R1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveRoomForId("R1", input)).isSameAs(existing);

        verify(repo).findRoomById("R1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRoomById_blankId_becomesInvalidRoomException() {
        assertThatThrownBy(() -> service.deleteRoomById(""))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining("ROM room id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void deleteRoomById_notFound_becomesRoomNotFoundException() {
        when(repo.existsById("R1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteRoomById("R1"))
                .isInstanceOf(RoomNotFoundException.class);

        verify(repo).existsById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRoomById_ok_deletes() {
        when(repo.existsById("R1")).thenReturn(true);

        service.deleteRoomById("R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRoomById_dataAccess_becomesRoomPersistenceException() {
        when(repo.existsById("R1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("R1");

        assertThatThrownBy(() -> service.deleteRoomById("R1"))
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining("Failed to delete ROM reset: R1");

        verify(repo).existsById("R1");
        verify(repo).deleteById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRooms_ok_returnsCount() {
        when(repo.count()).thenReturn(7L);

        assertThat(service.deleteAllRooms()).isEqualTo(7L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRooms_dataAccess_becomesRoomPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllRooms())
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining("Failed to delete all ROM rooms");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllRoomFallback_returnsEmptyList() throws Exception {
        var m = RoomService.class.getDeclaredMethod("getAllRoomFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Room> out = (List<Room>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getRoomsByIdFallback_throwsRoomPersistenceException() throws Exception {
        var m = RoomService.class.getDeclaredMethod("getRoomsByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "R1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: R1");

        verifyNoInteractions(repo);
    }
}
