package org.springy.som.modulith.agents.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springy.som.modulith.agents.api.AgentMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentControllerTest {
    @Mock
    private AgentService agentService;

    @InjectMocks
    private AgentController agentController;

    @Test
    void getAll_returnsEachStoredAgent() {
        AgentDocument guide = new AgentDocument();
        guide.setName("example-agent");
        guide.setEnabled(true);
        when(agentService.getAll()).thenReturn(List.of(guide));

        assertThat(agentController.getAll()).containsExactly(AgentMapper.toView(guide));
    }

    @Test
    void get_returnsTheStoredAgent() {
        AgentDocument document = new AgentDocument();
        document.setName("example-agent");
        document.setEnabled(true);
        when(agentService.get("example-agent")).thenReturn(document);

        assertThat(agentController.get("example-agent")).isEqualTo(AgentMapper.toView(document));
    }

    @Test
    void put_returnsTheSavedAgent() {
        AgentDocument document = new AgentDocument();
        document.setName("example-agent");
        document.setEnabled(false);
        when(agentService.save("example-agent", document)).thenReturn(document);

        assertThat(agentController.put("example-agent", document)).isEqualTo(AgentMapper.toView(document));
    }
}
