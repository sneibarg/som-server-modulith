package org.springy.som.modulith.prompt.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromptServiceTest {
    @Mock
    private PromptRepository promptRepository;

    private PromptService promptService;

    @BeforeEach
    void setUp() {
        promptService = new PromptService(promptRepository);
    }

    @Test
    void getAll_returnsTheStoredPrompts() {
        List<PromptDocument> stored = List.of(prompt("exits"));
        when(promptRepository.findAll()).thenReturn(stored);

        assertThat(promptService.getAll()).isSameAs(stored);
    }

    @Test
    void get_missing_throwsNotFound() {
        when(promptRepository.findAllByNameIgnoreCase("score")).thenReturn(List.of());

        assertThatThrownBy(() -> promptService.get("score"))
                .isInstanceOf(PromptNotFoundException.class)
                .hasMessage("No prompt stored for score");
    }

    @Test
    void get_blankName_throwsInvalid() {
        assertThatThrownBy(() -> promptService.get("  "))
                .isInstanceOf(InvalidPromptException.class)
                .hasMessage("name must not be blank");
        verifyNoInteractions(promptRepository);
    }

    @Test
    void get_matchesNameIgnoringCase() {
        PromptDocument stored = prompt("Score");
        when(promptRepository.findAllByNameIgnoreCase("score")).thenReturn(List.of(stored));

        assertThat(promptService.get(" score ")).isSameAs(stored);
    }

    @Test
    void save_requiresModel_andMatchingName() {
        PromptDocument missingModel = prompt("score");
        missingModel.setModel(" ");
        PromptDocument other = prompt("exits");

        assertThatThrownBy(() -> promptService.save("score", missingModel))
                .isInstanceOf(InvalidPromptException.class)
                .hasMessage("Prompt document requires name and model");
        assertThatThrownBy(() -> promptService.save("score", other))
                .isInstanceOf(InvalidPromptException.class)
                .hasMessage("name does not match the path");
        verifyNoInteractions(promptRepository);
    }

    @Test
    void save_rejectsATokenWithoutExampleOrText() {
        PromptDocument missingExample = prompt("score");
        missingExample.setTokens(Map.of("trust", Map.of("text", "trust level")));
        PromptDocument blankText = prompt("score");
        blankText.setTokens(Map.of("trust", token(" ", null)));

        assertThatThrownBy(() -> promptService.save("score", missingExample))
                .isInstanceOf(InvalidPromptException.class)
                .hasMessage("Prompt 'score' token 'trust' requires an example");
        assertThatThrownBy(() -> promptService.save("score", blankText))
                .isInstanceOf(InvalidPromptException.class)
                .hasMessage("Prompt 'score' token 'trust' requires text");
        verifyNoInteractions(promptRepository);
    }

    @Test
    void save_keepsANullExample_andCollapsesCaseVariants() {
        PromptDocument first = prompt("score");
        first.setId("kept-id");
        PromptDocument extra = prompt("SCORE");
        extra.setId("extra-id");
        when(promptRepository.findAllByNameIgnoreCase("score")).thenReturn(List.of(first, extra));
        PromptDocument body = prompt("Score");
        body.setSystem(null);
        body.setUser(null);
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("hit", 20);
        example.put("maxHit", 20);
        body.setTokens(new LinkedHashMap<>(Map.of("hitPoints", token("hit points", example), "trust", token("trust level", null))));
        when(promptRepository.save(body)).thenReturn(body);

        PromptDocument saved = promptService.save("score", body);

        assertThat(saved.getId()).isEqualTo("kept-id");
        assertThat(saved.getName()).isEqualTo("Score");
        assertThat(saved.getModel()).isEqualTo("gpt-oss:20b");
        assertThat(saved.getSystem()).isEmpty();
        assertThat(saved.getUser()).isEmpty();
        assertThat(saved.getTokens().get("trust")).containsEntry("example", null);
        assertThat(saved.getTokens().get("hitPoints").get("example")).isEqualTo(example);
        verify(promptRepository).deleteById("extra-id");
    }

    @Test
    void save_dataAccess_hidesTheDriverMessage() {
        when(promptRepository.findAllByNameIgnoreCase("score"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> promptService.save("score", prompt("score")))
                .isInstanceOf(PromptPersistenceException.class)
                .hasMessage("Failed to save prompt");
    }

    private static PromptDocument prompt(String name) {
        PromptDocument document = new PromptDocument();
        document.setName(name);
        document.setModel("gpt-oss:20b");
        document.setSystem("Read the text.");
        document.setUser("{score}");
        return document;
    }

    private static Map<String, Object> token(String text, Object example) {
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("text", text);
        spec.put("example", example);
        return spec;
    }
}
