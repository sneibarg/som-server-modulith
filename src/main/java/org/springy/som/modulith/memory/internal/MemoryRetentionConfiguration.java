package org.springy.som.modulith.memory.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MemoryRetentionProperties.class)
class MemoryRetentionConfiguration {
}
