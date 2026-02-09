package org.springy.som.modulith.domain.item.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.item.api.ItemApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.itemIdMissing;
import static org.springy.som.modulith.domain.DomainGuards.itemMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class ItemService implements ItemApi {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllItemsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<ItemDocument> getAllItems() {
        return itemRepository.findAll();
    }
    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ItemDocument getItemByName(@RequestParam String commandName) {
        return itemRepository.findItemById(commandName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ItemDocument getItemById(@RequestParam String commandId) {
        return itemRepository.findItemById(commandId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ItemDocument createItem(@Valid @RequestBody ItemDocument itemDocument) {
        requireEntityWithId(itemDocument, ItemDocument::getId, itemMissing(), itemIdMissing());

        try {
            // if (itemRepository.existsById(itemDocument.getId())) throw new ItemConflictException(...)
            return itemRepository.save(itemDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createItem itemId={}", safeId(itemDocument, ItemDocument::getId), ex);
            throw new ItemPersistenceException("Failed to create itemDocument"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public ItemDocument saveItemForId(String id, ItemDocument itemDocument) {
        requireText(id, itemIdMissing());
        requireEntityWithId(itemDocument, ItemDocument::getId, itemMissing(), itemIdMissing());

        return itemRepository.save(getItemById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteItemById(String id) {
        requireText(id, itemIdMissing());

        try {
            if (!itemRepository.existsById(id)) {
                throw new ItemNotFoundException(id);
            }
            itemRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteCommandById id={}", id, ex);
            throw new ItemPersistenceException("Failed to delete command: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllItems() {
        try {
            long itemCount = itemRepository.count();
            itemRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllCommands", ex);
            throw new ItemPersistenceException("Failed to delete all commands "+ ex);
        }
    }

    private List<ItemDocument> getAllItemsFallback(Throwable t) {
        log.warn("Fallback getAllItems due to {}", t.toString());
        return List.of();
    }

    private ItemDocument getItemByIdFallback(String id, Throwable t) {
        log.warn("Fallback getItemById id={} due to {}", id, t.toString());
        throw new ItemPersistenceException("ItemDocument lookup temporarily unavailable: " + id+" "+t);
    }
}
