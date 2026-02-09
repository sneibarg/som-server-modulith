package org.springy.som.modulith.domain.room.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    private final String roomIdMissing = "ROM room id must be provided";
    private final String roomMissing = "ROM room must be provided";
    private final String dbDown = "Service unavailable Failed to create ROM roomDocument: org.springframework.dao.DataAccessResourceFailureException: db down";

    @Mock
    private RoomRepository repo;
    private RoomService service;

    @BeforeEach
    void setUp() {
        service = new RoomService(repo);
    }

    @Test
    void getAllRooms_ok() {
        List<RoomDocument> roomDocuments = List.of(mock(RoomDocument.class), mock(RoomDocument.class));
        when(repo.findAll()).thenReturn(roomDocuments);

        assertThat(service.getAllRooms()).isSameAs(roomDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRoomByName_delegates() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(repo.findRoomById("Midgaard")).thenReturn(roomDocument);

        assertThat(service.getRoomByName("Midgaard")).isSameAs(roomDocument);

        verify(repo).findRoomById("Midgaard");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRoomById_delegates() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(repo.findRoomById("R1")).thenReturn(roomDocument);

        assertThat(service.getRoomById("R1")).isSameAs(roomDocument);

        verify(repo).findRoomById("R1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_null_becomesInvalidRoomException() {
        assertThatThrownBy(() -> service.createRoom(null))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining(roomMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRoom_blankId_becomesInvalidRoomException() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(roomDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createRoom(roomDocument))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining(roomIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createRoom_ok_saves() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(roomDocument.getId()).thenReturn("R1");
        when(repo.save(roomDocument)).thenReturn(roomDocument);

        assertThat(service.createRoom(roomDocument)).isSameAs(roomDocument);

        verify(repo).save(roomDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_dataAccess_becomesRoomPersistenceException() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(roomDocument.getId()).thenReturn("R1");
        when(repo.save(roomDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRoom(roomDocument))
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining(dbDown);

        verify(repo).save(roomDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRoom_dataAccess_safeIdExceptionPath_becomesRoomPersistenceException() {
        RoomDocument roomDocument = mock(RoomDocument.class);
        when(roomDocument.getId()).thenReturn("R1").thenThrow(new RuntimeException("boom"));
        when(repo.save(roomDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRoom(roomDocument))
                .isInstanceOf(RoomPersistenceException.class)
                .hasMessageContaining(dbDown);

        verify(repo).save(roomDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveRoomForId_blankId_becomesInvalidRoomException() {
        RoomDocument roomDocument = mock(RoomDocument.class);

        assertThatThrownBy(() -> service.saveRoomForId(" ", roomDocument))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining(roomIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRoomForId_nullRoom_becomesInvalidRoomException() {
        assertThatThrownBy(() -> service.saveRoomForId("R1", null))
                .isInstanceOf(InvalidRoomException.class)
                .hasMessageContaining(roomMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveRoomForId_ok_savesByLookup() {
        RoomDocument input = mock(RoomDocument.class);
        when(input.getId()).thenReturn("R1");

        RoomDocument existing = mock(RoomDocument.class);
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
                .hasMessageContaining(roomIdMissing);

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
    void getAllRoomsFallback_returnsEmptyList() throws Exception {
        var m = RoomService.class.getDeclaredMethod("getAllRoomsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<RoomDocument> out = (List<RoomDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getRoomByIdFallback_throwsRoomPersistenceException() throws Exception {
        var m = RoomService.class.getDeclaredMethod("getRoomByIdFallback", String.class, Throwable.class);
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
