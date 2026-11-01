package org.springy.som.modulith.memory.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.moments.HourHasPassed;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoryExpirationListenerTest {
    @Mock
    private MemoryRepository memoryRepository;

    @Test
    void hourHasPassed_deletesMissingSavedAtAndDocumentsBeforeTheCutoff() {
        MemoryExpirationListener listener = new MemoryExpirationListener(
                memoryRepository,
                new MemoryRetentionProperties(Duration.ofHours(1)));

        listener.on(HourHasPassed.of(LocalDateTime.parse("2026-11-01T13:00:00")));

        verify(memoryRepository).deleteBySavedAtIsNull();
        verify(memoryRepository).deleteBySavedAtBefore(Instant.parse("2026-11-01T13:00:00Z"));
    }

    @Test
    void listensWithTheModuleListener() throws Exception {
        Method method = MemoryExpirationListener.class.getMethod("on", HourHasPassed.class);

        assertThat(method.getAnnotation(ApplicationModuleListener.class)).isNotNull();
    }
}
