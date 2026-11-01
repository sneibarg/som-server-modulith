package org.springy.som.modulith.memory.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemoryServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-01T14:00:00Z");
    private static final Duration RETENTION = Duration.ofHours(1);

    @Mock
    private MemoryRepository memoryRepository;

    private MemoryService memoryService;

    @BeforeEach
    void setUp() {
        memoryService = new MemoryService(
                memoryRepository,
                new MemoryRetentionProperties(RETENTION),
                Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void get_missing_throwsNotFound() {
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of());

        assertThatThrownBy(() -> memoryService.get("Ada"))
                .isInstanceOf(MemoryNotFoundException.class)
                .hasMessage("No memory stored for Ada");
    }

    @Test
    void get_blankName_throwsInvalid() {
        assertThatThrownBy(() -> memoryService.get(" "))
                .isInstanceOf(InvalidMemoryException.class)
                .hasMessage("agentName must not be blank");
        verifyNoInteractions(memoryRepository);
    }

    @Test
    void get_expiredOrMissingSavedAt_throwsNotFound() {
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of(
                memory("Ada", NOW.minus(RETENTION).minusSeconds(1)),
                memory("Ada", null)));

        assertThatThrownBy(() -> memoryService.get("Ada"))
                .isInstanceOf(MemoryNotFoundException.class);
    }

    @Test
    void get_savedAtEqualToCutoff_isReturned() {
        MemoryDocument kept = memory("Ada", NOW.minus(RETENTION));
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of(
                memory("Ada", null),
                kept));

        assertThat(memoryService.get("Ada")).isSameAs(kept);
    }

    @Test
    void get_dataAccess_hidesTheDriverMessage() {
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> memoryService.get("Ada"))
                .isInstanceOf(MemoryPersistenceException.class)
                .hasMessage("Failed to load memory");
    }

    @Test
    void save_blankNames_areRejected() {
        MemoryDocument blank = new MemoryDocument();
        blank.setAgentName(" ");

        assertThatThrownBy(() -> memoryService.save(" ", new MemoryDocument()))
                .isInstanceOf(InvalidMemoryException.class);
        assertThatThrownBy(() -> memoryService.save("Ada", blank))
                .isInstanceOf(InvalidMemoryException.class);
        verifyNoInteractions(memoryRepository);
    }

    @Test
    void save_differentName_isRejected() {
        MemoryDocument document = new MemoryDocument();
        document.setAgentName("Bob");

        assertThatThrownBy(() -> memoryService.save("Ada", document))
                .isInstanceOf(InvalidMemoryException.class)
                .hasMessage("agentName does not match the path");
        verifyNoInteractions(memoryRepository);
    }

    @Test
    void save_replacesOneDocument_andAssignsSavedAt() {
        MemoryDocument first = memory("ada", Instant.EPOCH);
        first.setId("kept-id");
        MemoryDocument extra = memory("ADA", Instant.EPOCH);
        extra.setId("extra-id");
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of(first, extra));
        MemoryDocument body = new MemoryDocument();
        body.setAgentName("Ada");
        body.setSavedAt(Instant.EPOCH);
        body.setPlayers(null);
        body.setMobiles(new ArrayList<>(List.of(being("goblin"))));
        when(memoryRepository.save(body)).thenReturn(body);

        MemoryDocument saved = memoryService.save("Ada", body);

        assertThat(saved.getId()).isEqualTo("kept-id");
        assertThat(saved.getAgentName()).isEqualTo("Ada");
        assertThat(saved.getSavedAt()).isEqualTo(NOW);
        assertThat(saved.getPlayers()).isEmpty();
        assertThat(saved.getMobiles()).extracting(MemoryDocument.Being::getName).containsExactly("goblin");
        assertThat(saved.getMobiles().get(0).getAuras()).isEmpty();
        verify(memoryRepository).deleteById("extra-id");
    }

    @Test
    void save_omittedName_usesThePath() {
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of());
        MemoryDocument body = new MemoryDocument();
        when(memoryRepository.save(body)).thenReturn(body);

        MemoryDocument saved = memoryService.save("Ada", body);

        assertThat(saved.getId()).isNull();
        assertThat(saved.getAgentName()).isEqualTo("Ada");
        assertThat(saved.getSavedAt()).isEqualTo(NOW);
    }

    @Test
    void save_ofExpiredDocument_setsANewSavedAt() {
        MemoryDocument existing = memory("Ada", NOW.minus(Duration.ofDays(3)));
        existing.setId("old-id");
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada")).thenReturn(List.of(existing));
        MemoryDocument body = new MemoryDocument();
        body.setAgentName("ada");
        body.setSavedAt(Instant.EPOCH);
        when(memoryRepository.save(body)).thenReturn(body);

        MemoryDocument saved = memoryService.save("Ada", body);

        assertThat(saved.getId()).isEqualTo("old-id");
        assertThat(saved.getAgentName()).isEqualTo("ada");
        assertThat(saved.getSavedAt()).isEqualTo(NOW);
    }

    @Test
    void save_dataAccess_hidesTheDriverMessage() {
        when(memoryRepository.findAllByAgentNameIgnoreCase("Ada"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> memoryService.save("Ada", new MemoryDocument()))
                .isInstanceOf(MemoryPersistenceException.class)
                .hasMessage("Failed to save memory");
    }

    private static MemoryDocument memory(String agentName, Instant savedAt) {
        MemoryDocument document = new MemoryDocument();
        document.setAgentName(agentName);
        document.setSavedAt(savedAt);
        return document;
    }

    private static MemoryDocument.Being being(String name) {
        MemoryDocument.Being being = new MemoryDocument.Being();
        being.setName(name);
        being.setAuras(null);
        return being;
    }
}
