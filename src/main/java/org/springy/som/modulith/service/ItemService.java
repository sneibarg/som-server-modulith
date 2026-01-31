package org.springy.som.modulith.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.item.Item;
import org.springy.som.modulith.exception.item.ItemNotFoundException;
import org.springy.som.modulith.exception.item.ItemPersistenceException;
import org.springy.som.modulith.repository.ItemRepository;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.itemIdMissing;
import static org.springy.som.modulith.util.DomainGuards.itemMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllItemsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Item getItemByName(@RequestParam String commandName) {
        return itemRepository.findItemById(commandName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Item getItemById(@RequestParam String commandId) {
        return itemRepository.findItemById(commandId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Item createItem(@Valid @RequestBody Item item) {
        requireEntityWithId(item, Item::getId, itemMissing(), itemIdMissing());

        try {
            // if (itemRepository.existsById(item.getId())) throw new ItemConflictException(...)
            return itemRepository.save(item);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createCommand areaId={}", safeId(item, Item::getId), ex);
            throw new ItemPersistenceException("Failed to create item"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Item saveItemForId(String id, Item item) {
        requireText(id, itemIdMissing());
        requireEntityWithId(item, Item::getId, itemMissing(), itemIdMissing());

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

    private List<Item> getAllItemsFallback(Throwable t) {
        log.warn("Fallback getAllItems due to {}", t.toString());
        return List.of();
    }

    private Item getRomClassByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllItemsById id={} due to {}", id, t.toString());
        throw new ItemPersistenceException("Item lookup temporarily unavailable: " + id+" "+t);
    }
}
