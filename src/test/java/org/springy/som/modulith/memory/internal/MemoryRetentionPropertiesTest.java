package org.springy.som.modulith.memory.internal;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemoryRetentionPropertiesTest {
    @Test
    void bindsSomMemoryRetention() {
        ConfigurationProperties annotation = MemoryRetentionProperties.class.getAnnotation(ConfigurationProperties.class);

        assertThat(annotation.prefix()).isEqualTo("som.memory");
        assertThat(new MemoryRetentionProperties(Duration.ofDays(1)).retention()).isEqualTo(Duration.ofDays(1));
    }

    @Test
    void rejectsMissingZeroOrNegative() {
        assertThatThrownBy(() -> new MemoryRetentionProperties(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("som.memory.retention must be a positive duration");
        assertThatThrownBy(() -> new MemoryRetentionProperties(Duration.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new MemoryRetentionProperties(Duration.ofMinutes(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
