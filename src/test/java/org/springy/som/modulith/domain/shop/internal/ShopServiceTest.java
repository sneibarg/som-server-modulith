package org.springy.som.modulith.domain.shop.internal;

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
class ShopServiceTest {
    private final String shopIdMissing = "ROM shop id must be provided";
    private final String shopMissing = "ROM shop must be provided";

    @Mock
    private ShopRepository repo;
    private ShopService service;

    @BeforeEach
    void setUp() {
        service = new ShopService(repo);
    }

    @Test
    void getAllShops_ok() {
        List<ShopDocument> shopDocuments = List.of(mock(ShopDocument.class), mock(ShopDocument.class));
        when(repo.findAll()).thenReturn(shopDocuments);

        assertThat(service.getAllShops()).isSameAs(shopDocuments);

        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getShopByName_delegates() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(repo.findShopById("General Store")).thenReturn(shopDocument);

        assertThat(service.getShopByName("General Store")).isSameAs(shopDocument);

        verify(repo).findShopById("General Store");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getShopById_delegates() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(repo.findShopById("S1")).thenReturn(shopDocument);

        assertThat(service.getShopById("S1")).isSameAs(shopDocument);

        verify(repo).findShopById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createShop_null_becomesInvalidShopException() {
        assertThatThrownBy(() -> service.createShop(null))
                .isInstanceOf(InvalidShopException.class)
                .hasMessageContaining(shopMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createShop_blankId_becomesInvalidShopException() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(shopDocument.getId()).thenReturn("");

        assertThatThrownBy(() -> service.createShop(shopDocument))
                .isInstanceOf(InvalidShopException.class)
                .hasMessageContaining(shopIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void createShop_ok_saves() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(shopDocument.getId()).thenReturn("S1");
        when(repo.save(shopDocument)).thenReturn(shopDocument);

        assertThat(service.createShop(shopDocument)).isSameAs(shopDocument);

        verify(repo).save(shopDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createShop_dataAccess_becomesShopPersistenceException() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(shopDocument.getId()).thenReturn("S1");
        when(repo.save(shopDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createShop(shopDocument))
                .isInstanceOf(ShopPersistenceException.class)
                .hasMessageContaining("Failed to create ROM shopDocument");

        verify(repo).save(shopDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createShop_dataAccess_safeIdExceptionPath_becomesShopPersistenceException() {
        ShopDocument shopDocument = mock(ShopDocument.class);
        when(shopDocument.getId()).thenReturn("S1").thenThrow(new RuntimeException("boom"));
        when(repo.save(shopDocument)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createShop(shopDocument))
                .isInstanceOf(ShopPersistenceException.class)
                .hasMessageContaining("Failed to create ROM shopDocument");

        verify(repo).save(shopDocument);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void saveShopForId_blankId_becomesInvalidShopException() {
        ShopDocument shopDocument = mock(ShopDocument.class);

        assertThatThrownBy(() -> service.saveShopForId(" ", shopDocument))
                .isInstanceOf(InvalidShopException.class)
                .hasMessageContaining(shopIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveShopForId_nullShop_becomesInvalidShopException() {
        assertThatThrownBy(() -> service.saveShopForId("S1", null))
                .isInstanceOf(InvalidShopException.class)
                .hasMessageContaining(shopMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void saveShopForId_ok_savesByLookup() {
        ShopDocument input = mock(ShopDocument.class);
        when(input.getId()).thenReturn("S1");

        ShopDocument existing = mock(ShopDocument.class);
        when(repo.findShopById("S1")).thenReturn(existing);
        when(repo.save(existing)).thenReturn(existing);

        assertThat(service.saveShopForId("S1", input)).isSameAs(existing);

        verify(repo).findShopById("S1");
        verify(repo).save(existing);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteShopById_blankId_becomesInvalidShopException() {
        assertThatThrownBy(() -> service.deleteShopById(""))
                .isInstanceOf(InvalidShopException.class)
                .hasMessageContaining(shopIdMissing);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteShopById_notFound_becomesShopNotFoundException() {
        when(repo.existsById("S1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteShopById("S1"))
                .isInstanceOf(ShopNotFoundException.class);

        verify(repo).existsById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteShopById_ok_deletes() {
        when(repo.existsById("S1")).thenReturn(true);

        service.deleteShopById("S1");

        verify(repo).existsById("S1");
        verify(repo).deleteById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteShopById_dataAccess_becomesShopPersistenceException() {
        when(repo.existsById("S1")).thenReturn(true);
        doThrow(new DataAccessResourceFailureException("db down")).when(repo).deleteById("S1");

        assertThatThrownBy(() -> service.deleteShopById("S1"))
                .isInstanceOf(ShopPersistenceException.class)
                .hasMessageContaining("Failed to delete ROM shop: S1");

        verify(repo).existsById("S1");
        verify(repo).deleteById("S1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllShops_ok_returnsCount() {
        when(repo.count()).thenReturn(9L);

        assertThat(service.deleteAllShops()).isEqualTo(9L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllShops_dataAccess_becomesShopPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllShops())
                .isInstanceOf(ShopPersistenceException.class)
                .hasMessageContaining("Failed to delete all ROM rooms");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllShopsFallback_returnsEmptyList() throws Exception {
        var m = ShopService.class.getDeclaredMethod("getAllShopsFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ShopDocument> out = (List<ShopDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(out).isEmpty();
        verifyNoInteractions(repo);
    }

    @Test
    void getShopByIdFallback_throwsShopPersistenceException() throws Exception {
        var m = ShopService.class.getDeclaredMethod("getShopByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "S1", new RuntimeException("cb"));
            } catch (java.lang.reflect.InvocationTargetException e) {
                throw e.getCause();
            }
        }).isInstanceOf(ShopPersistenceException.class)
                .hasMessageContaining("temporarily unavailable: S1");

        verifyNoInteractions(repo);
    }
}
