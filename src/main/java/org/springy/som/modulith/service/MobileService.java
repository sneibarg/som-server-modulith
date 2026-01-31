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
import org.springy.som.modulith.domain.command.Command;
import org.springy.som.modulith.domain.mobile.Mobile;
import org.springy.som.modulith.exception.mobile.MobileNotFoundException;
import org.springy.som.modulith.exception.mobile.MobilePersistenceException;
import org.springy.som.modulith.repository.MobileRepository;

import java.util.List;

import static org.springy.som.modulith.util.DomainGuards.mobileIdMissing;
import static org.springy.som.modulith.util.DomainGuards.mobileMissing;
import static org.springy.som.modulith.util.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.util.ServiceGuards.requireText;
import static org.springy.som.modulith.util.ServiceGuards.safeId;

@Slf4j
@Service
public class MobileService {
    private final MobileRepository mobileRepository;

    public MobileService(MobileRepository mobileRepository) {
        this.mobileRepository = mobileRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllMobilesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<Mobile> getAllMobiles() {
        return mobileRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Mobile getMobileByName(@RequestParam String mobileName) {
        return mobileRepository.findMobileByName(mobileName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Mobile getMobileById(@RequestParam String itemId) {
        return mobileRepository.findMobileById(itemId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Mobile createMobile(@Valid @RequestBody Mobile mobile) {
        requireEntityWithId(mobile, Mobile::getId, mobileMissing(), mobileIdMissing());

        try {
            // if (mobileRepository.existsById(mobile.getMobileId())) throw new MobileConflictException(...)
            return mobileRepository.save(mobile);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createCommand areaId={}", safeId(mobile, Mobile::getId), ex);
            throw new MobilePersistenceException("Failed to create command"+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public Mobile saveMobileForId(String id, Mobile mobile) {
        requireText(id, mobileIdMissing());
        requireEntityWithId(mobile, Mobile::getId, mobileMissing(), mobileIdMissing());

        return mobileRepository.save(getMobileById(id));
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteMobileById(String id) {
        requireText(id, mobileIdMissing());

        try {
            if (!mobileRepository.existsById(id)) {
                throw new MobileNotFoundException(id);
            }
            mobileRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteMobileById id={}", id, ex);
            throw new MobilePersistenceException("Failed to delete command: " + id+" "+ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllMobiles() {
        try {
            long itemCount = mobileRepository.count();
            mobileRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllMobiles", ex);
            throw new MobilePersistenceException("Failed to delete all commands "+ ex);
        }
    }

    private List<Mobile> getAllMobilesFallback(Throwable t) {
        log.warn("Fallback getAllMobiles due to {}", t.toString());
        return List.of();
    }

    private Command getMobileByIdFallback(String id, Throwable t) {
        log.warn("Fallback getMobileById id={} due to {}", id, t.toString());
        throw new MobilePersistenceException("Mobile lookup temporarily unavailable: " + id+" "+t);
    }
}
