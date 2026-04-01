package org.springy.som.modulith.domain.command.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.command.api.SocialApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.socialIdMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class SocialService implements SocialApi {
    private final SocialRepository socialRepository;

    public SocialService(SocialRepository socialRepository) {
        this.socialRepository = socialRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllSocialsFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<SocialDocument> getAllSocials() {
        return socialRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SocialDocument getSocialByName(@RequestParam String socialName) {
        return socialRepository.findSocialByName(socialName);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SocialDocument getSocialById(@RequestParam String socialId) {
        return socialRepository.findSocialById(socialId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SocialDocument createSocial(@Valid @RequestBody SocialDocument socialDocument) {
        requireEntityWithId(socialDocument, SocialDocument::getId, socialIdMissing(), socialIdMissing());

        try {
            // if (socialRepository.existsById(socialDocument.getId())) throw new SocialConflictException(...)
            return socialRepository.save(socialDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createSocial socialId={}", safeId(socialDocument, SocialDocument::getId), ex);
            throw new SocialPersistenceException("Failed to create socialDocument" + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public SocialDocument saveSocialForId(String id, SocialDocument socialDocument) {
        SocialDocument existing = socialRepository.findSocialById(id);
        requireText(existing.getId(), socialIdMissing());
        requireEntityWithId(existing, SocialDocument::getId, socialIdMissing(), socialIdMissing());

        return socialRepository.save(socialDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteSocialById(String id) {
        requireText(id, socialIdMissing());

        try {
            if (!socialRepository.existsById(id)) {
                throw new SocialNotFoundException(id);
            }
            socialRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteSocialById id={}", id, ex);
            throw new SocialPersistenceException("Failed to delete social: " + id + " " + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllSocials() {
        try {
            long itemCount = socialRepository.count();
            socialRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllSocials", ex);
            throw new SocialPersistenceException("Failed to delete all socials " + ex);
        }
    }

    private List<SocialDocument> getAllSocialsFallback(Throwable t) {
        log.warn("Fallback getAllSocials due to {}", t.toString());
        return List.of();
    }

    private SocialDocument getSocialByIdFallback(String id, Throwable t) {
        log.warn("Fallback getSocialById id={} due to {}", id, t.toString());
        throw new SocialPersistenceException("SocialDocument lookup temporarily unavailable: " + id + " " + t);
    }
}
