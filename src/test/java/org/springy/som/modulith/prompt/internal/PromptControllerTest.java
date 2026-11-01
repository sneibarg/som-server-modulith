package org.springy.som.modulith.prompt.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.prompt.api.PromptMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptControllerTest {
    @Mock
    private PromptService promptService;

    @InjectMocks
    private PromptController promptController;

    @Test
    void getAll_returnsEachStoredPrompt() {
        PromptDocument exits = new PromptDocument();
        exits.setName("exits");
        when(promptService.getAll()).thenReturn(List.of(exits));

        assertThat(promptController.getAll()).containsExactly(PromptMapper.toView(exits));
    }

    @Test
    void get_returnsTheStoredPrompt() {
        PromptDocument document = new PromptDocument();
        document.setName("score");
        when(promptService.get("score")).thenReturn(document);

        assertThat(promptController.get("score")).isEqualTo(PromptMapper.toView(document));
    }

    @Test
    void put_returnsTheSavedPrompt() {
        PromptDocument document = new PromptDocument();
        document.setName("score");
        when(promptService.save("score", document)).thenReturn(document);

        assertThat(promptController.put("score", document)).isEqualTo(PromptMapper.toView(document));
    }
}
