package org.springy.som.modulith.memory.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.memory.api.MemoryMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemoryControllerTest {
    @Mock
    private MemoryService memoryService;

    @InjectMocks
    private MemoryController memoryController;

    @Test
    void get_returnsTheStoredDocument() {
        MemoryDocument document = new MemoryDocument();
        when(memoryService.get("Ada")).thenReturn(document);

        assertThat(memoryController.get("Ada")).isEqualTo(MemoryMapper.toView(document));
    }

    @Test
    void put_returnsTheSavedDocument() {
        MemoryDocument document = new MemoryDocument();
        when(memoryService.save("Ada", document)).thenReturn(document);

        assertThat(memoryController.put("Ada", document)).isEqualTo(MemoryMapper.toView(document));
    }
}
