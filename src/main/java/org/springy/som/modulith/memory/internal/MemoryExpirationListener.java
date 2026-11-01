package org.springy.som.modulith.memory.internal;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.moments.HourHasPassed;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

@Component
public class MemoryExpirationListener {
    private final MemoryRepository memoryRepository;
    private final MemoryRetentionProperties retention;

    public MemoryExpirationListener(MemoryRepository memoryRepository, MemoryRetentionProperties retention) {
        this.memoryRepository = memoryRepository;
        this.retention = retention;
    }

    @ApplicationModuleListener
    public void on(HourHasPassed hour) {
        Instant cutoff = hour.getTime().plusHours(1).toInstant(ZoneOffset.UTC).minus(retention.retention());
        memoryRepository.deleteBySavedAtIsNull();
        memoryRepository.deleteBySavedAtBefore(cutoff);
    }
}
