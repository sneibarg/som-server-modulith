package org.springy.som.modulith.memory.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "som.memory")
public record MemoryRetentionProperties(Duration retention) {
    public MemoryRetentionProperties {
        if (retention == null || retention.isZero() || retention.isNegative()) {
            throw new IllegalArgumentException("som.memory.retention must be a positive duration");
        }
    }
}
