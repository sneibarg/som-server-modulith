package org.springy.som.modulith.service;


import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.domain.player.PlayerAccount;
import org.springy.som.modulith.exception.player.InvalidPlayerException;
import org.springy.som.modulith.exception.player.PlayerNotFoundException;
import org.springy.som.modulith.exception.player.PlayerPersistenceException;
import org.springy.som.modulith.repository.PlayerAccountRepository;

import java.util.List;

@Slf4j
@Service
public class PlayerService {
    private final PlayerAccountRepository playerAccountRepository;

    public PlayerService(PlayerAccountRepository playerAccountRepository) {
        this.playerAccountRepository = playerAccountRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllPlayerAccountsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<PlayerAccount> getAllPlayerAccounts() {
        return playerAccountRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerAccount getPlayerAccountByName(@RequestParam String accountName) {
        return playerAccountRepository.findPlayerAccountByName(accountName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerAccount getPlayerAccountById(@RequestParam String id) {
        return playerAccountRepository.findPlayerAccountById(id);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerAccount createPlayerAccount(@Valid @RequestBody PlayerAccount playerAccount) {
        requirePlayerAccount(playerAccount);

        try {
            // if (playerAccountRepository.existsById(mobile.getPlayerAccountId())) throw new PlayerAccountConflictException(...)
            return playerAccountRepository.save(playerAccount);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createPlayerAccount areaId={}", safeId(playerAccount), ex);
            throw new PlayerPersistenceException("Failed to create player account"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public PlayerAccount savePlayerAccountForId(String id, PlayerAccount playerAccount) {
        requireId(id);
        requirePlayerAccount(playerAccount);

        return playerAccountRepository.save(getPlayerAccountById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deletePlayerAccountById(String id) {
        requireId(id);

        try {
            if (!playerAccountRepository.existsById(id)) {
                throw new PlayerNotFoundException(id);
            }
            playerAccountRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteMobileById id={}", id, ex);
            throw new PlayerPersistenceException("Failed to delete command: " + id+" "+ex);
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
            log.warn("DB failure in deleteAllMobiles", ex);
            throw new PlayerPersistenceException("Failed to delete all commands "+ ex);
        }
    }

    private static void requireId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new InvalidPlayerException("Mobile id must be provided");
        }
    }

    private static void requirePlayerAccount(PlayerAccount playerAccount) {
        if (playerAccount == null) {
            throw new InvalidPlayerException("Mobile must be provided");
        }

        requireId(playerAccount.getId());
    }

    private static String safeId(PlayerAccount playerAccount) {
        try {
            return playerAccount.getId();
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<PlayerAccount> getAllPlayerAccountsFallback(Throwable t) {
        log.warn("Fallback getAllPlayerAccounts due to {}", t.toString());
        return List.of();
    }

    private Command getPlayerAccountByIdFallback(String id, Throwable t) {
        log.warn("Fallback getAllPlayerAccountsById id={} due to {}", id, t.toString());
        throw new PlayerPersistenceException("Mobile lookup temporarily unavailable: " + id+" "+t);
    }
}
