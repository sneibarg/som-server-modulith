package org.springy.som.modulith.domain.player.internal;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.player.api.PlayerApi;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.playerAccountIdMissing;
import static org.springy.som.modulith.util.DomainGuards.playerAccountMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class PlayerService implements PlayerApi {
    private final PlayerAccountRepository playerAccountRepository;

    public PlayerService(PlayerAccountRepository playerAccountRepository) {
        this.playerAccountRepository = playerAccountRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllPlayerAccountsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<PlayerDocument> getAllPlayerAccounts() {
        return playerAccountRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerDocument getPlayerAccountByName(@RequestParam String accountName) {
        return playerAccountRepository.findPlayerAccountByName(accountName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerDocument getPlayerAccountById(@RequestParam String id) {
        return playerAccountRepository.findPlayerAccountById(id);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerDocument createPlayerAccount(@Valid @RequestBody PlayerDocument playerDocument) {
        requireEntityWithId(playerDocument, PlayerDocument::getId, playerAccountMissing(), playerAccountIdMissing());

        try {
            // if (playerAccountRepository.existsById(mobile.getPlayerAccountId())) throw new PlayerAccountConflictException(...)
            return playerAccountRepository.save(playerDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createPlayerAccount playerAccountId={}", safeId(playerDocument, PlayerDocument::getId), ex);
            throw new PlayerPersistenceException("Failed to create player account"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerDocument savePlayerAccountForId(String id, PlayerDocument playerDocument) {
        requireText(id, playerAccountIdMissing());
        requireEntityWithId(playerDocument, PlayerDocument::getId, playerAccountMissing(), playerAccountIdMissing());

        return playerAccountRepository.save(getPlayerAccountById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deletePlayerAccountById(String id) {
        requireText(id, playerAccountIdMissing());

        try {
            if (!playerAccountRepository.existsById(id)) {
                throw new PlayerNotFoundException(id);
            }
            playerAccountRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deletePlayerAccountById id={}", id, ex);
            throw new PlayerPersistenceException("Failed to delete player account: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllPlayerAccounts() {
        try {
            long itemCount = playerAccountRepository.count();
            playerAccountRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllPlayerAccounts", ex);
            throw new PlayerPersistenceException("Failed to delete all player accounts "+ ex);
        }
    }

    private List<PlayerDocument> getAllPlayerAccountsFallback(Throwable t) {
        log.warn("Fallback getAllPlayerAccounts due to {}", t.toString());
        return List.of();
    }

    private PlayerDocument getPlayerAccountByIdFallback(String id, Throwable t) {
        log.warn("Fallback getPlayerAccountById id={} due to {}", id, t.toString());
        throw new PlayerPersistenceException("PlayerDocument lookup temporarily unavailable: " + id+" "+t);
    }
}
