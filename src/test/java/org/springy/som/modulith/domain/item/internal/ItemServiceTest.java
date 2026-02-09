package org.springy.som.modulith.domain.item.internal;

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
class ItemServiceTest {
    private final String romItemMissing = "ROM item must be provided";
    private final String romItemIdMissing = "ROM item id must be provided";

    @Mock ItemRepository repo;

    private ItemService service;

    @BeforeEach
    void setUp() {
        service = new ItemService(repo);
    }

    @Test
    void getAllItems_ok() {
        List<ItemDocument> itemDocuments = List.of(mock(ItemDocument.class), mock(ItemDocument.class));
        when(repo.findAll()).thenReturn(itemDocuments);

        assertThat(service.getAllItems()).isSameAs(itemDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getItemByName_delegatesToFindItemById() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(repo.findItemById("sword")).thenReturn(itemDocument);

        assertThat(service.getItemByName("sword")).isSameAs(itemDocument);

        verify(repo).findItemById("sword");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getItemById_delegatesToFindItemById() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(repo.findItemById("I1")).thenReturn(itemDocument);

        assertThat(service.getItemById("I1")).isSameAs(itemDocument);

        verify(repo).findItemById("I1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createItem_null_becomesInvalidItemException() {
        assertThatThrownBy(() -> service.createItem(null))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining(romItemMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createItem_blankId_becomesInvalidItemException() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(itemDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createItem(itemDocument))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining(romItemIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createItem_ok_saves() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(itemDocument.getId()).thenReturn("I1");
        when(repo.save(itemDocument)).thenReturn(itemDocument);

        assertThat(service.createItem(itemDocument)).isSameAs(itemDocument);

        verify(repo).save(itemDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createItem_dataAccess_becomesItemPersistenceException() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(itemDocument.getId()).thenReturn("I1");
        when(repo.save(itemDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createItem(itemDocument))
                .isInstanceOf(ItemPersistenceException.class)
                .hasMessageContaining("Failed to create itemDocument");

        verify(repo).save(itemDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createItem_dataAccess_safeIdExceptionPath_becomesItemPersistenceException() {
        ItemDocument itemDocument = mock(ItemDocument.class);
        when(itemDocument.getId()).thenReturn("I1").thenThrow(new RuntimeException("boom"));
        when(repo.save(itemDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createItem(itemDocument))
                .isInstanceOf(ItemPersistenceException.class)
                .hasMessageContaining("Failed to create itemDocument");

        verify(repo).save(itemDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveItemForId_blankId_becomesInvalidItemException() {
        ItemDocument itemDocument = mock(ItemDocument.class);

        assertThatThrownBy(() -> service.saveItemForId(" ", itemDocument))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining(romItemIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveItemForId_nullItem_becomesInvalidItemException() {
        assertThatThrownBy(() -> service.saveItemForId("I1", null))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining(romItemMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveItemForId_ok_savesItemByLookup() {
        ItemDocument input = mock(ItemDocument.class);
        when(input.getId()).thenReturn("I1");

        ItemDocument existing = mock(ItemDocument.class);
        when(repo.findItemById("I1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveItemForId("I1", input)).isSameAs(existing);

        verify(repo).findItemById("I1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteItemById_blankId_becomesInvalidItemException() {
        assertThatThrownBy(() -> service.deleteItemById(""))
                .isInstanceOf(InvalidItemException.class)
                .hasMessageContaining(romItemIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteItemById_notFound_becomesItemNotFoundException() {
        when(repo.existsById("I1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteItemById("I1"))
                .isInstanceOf(ItemNotFoundException.class);

        verify(repo).existsById("I1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteItemById_ok_deletes() {
        when(repo.existsById("I1")).thenReturn(true);

        service.deleteItemById("I1");

        verify(repo).existsById("I1");
        verify(repo).deleteById("I1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteItemById_dataAccess_becomesItemPersistenceException() {
        when(repo.existsById("I1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("I1");

        assertThatThrownBy(() -> service.deleteItemById("I1"))
                .isInstanceOf(ItemPersistenceException.class)
                .hasMessageContaining("Failed to delete command: I1");

        verify(repo).existsById("I1");
        verify(repo).deleteById("I1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllItems_ok_returnsCount() {
        when(repo.count()).thenReturn(7L);

        assertThat(service.deleteAllItems()).isEqualTo(7L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllItems_dataAccess_becomesItemPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllItems())
                .isInstanceOf(ItemPersistenceException.class)
                .hasMessageContaining("Failed to delete all commands");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllItemsFallback_returnsEmptyList() throws Exception {
        var m = ItemService.class.getDeclaredMethod("getAllItemsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ItemDocument> out = (List<ItemDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getRomClassByIdFallback_throwsItemPersistenceException() throws Exception {
        var m = ItemService.class.getDeclaredMethod("getItemByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "I1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(ItemPersistenceException.class)
                .hasMessageContaining("ItemDocument lookup temporarily unavailable: I1");

        verifyNoInteractions(repo);
    }
}
