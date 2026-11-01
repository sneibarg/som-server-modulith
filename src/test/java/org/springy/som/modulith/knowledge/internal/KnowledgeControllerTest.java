package org.springy.som.modulith.knowledge.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.knowledge.api.KnowledgeMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeControllerTest {
    @Mock
    private KnowledgeService knowledgeService;

    @InjectMocks
    private KnowledgeController knowledgeController;

    @Test
    void get_returnsTheStoredDocument() {
        KnowledgeDocument document = new KnowledgeDocument();
        when(knowledgeService.get("hero")).thenReturn(document);

        assertThat(knowledgeController.get("hero")).isEqualTo(KnowledgeMapper.toView(document));
    }

    @Test
    void put_returnsTheSavedDocument() {
        KnowledgeDocument document = new KnowledgeDocument();
        when(knowledgeService.save("hero", document)).thenReturn(document);

        assertThat(knowledgeController.put("hero", document)).isEqualTo(KnowledgeMapper.toView(document));
        verify(knowledgeService).save("hero", document);
    }
}
