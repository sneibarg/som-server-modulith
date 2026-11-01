package org.springy.som.modulith.agents.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {
    @Mock
    private AgentRepository agentRepository;

    private AgentService agentService;

    @BeforeEach
    void setUp() {
        agentService = new AgentService(agentRepository);
    }

    @Test
    void getAll_returnsTheStoredAgents() {
        List<AgentDocument> stored = List.of(agent("example-agent"));
        when(agentRepository.findAll()).thenReturn(stored);

        assertThat(agentService.getAll()).isSameAs(stored);
    }

    @Test
    void get_missing_throwsNotFound() {
        when(agentRepository.findAllByNameIgnoreCase("example-agent")).thenReturn(List.of());

        assertThatThrownBy(() -> agentService.get("example-agent"))
                .isInstanceOf(AgentNotFoundException.class)
                .hasMessage("No agent stored for example-agent");
    }

    @Test
    void get_blankName_throwsInvalid() {
        assertThatThrownBy(() -> agentService.get(" "))
                .isInstanceOf(InvalidAgentException.class)
                .hasMessage("name must not be blank");
        verifyNoInteractions(agentRepository);
    }

    @Test
    void get_matchesNameIgnoringCase() {
        AgentDocument stored = agent("Example-Agent");
        when(agentRepository.findAllByNameIgnoreCase("example-agent")).thenReturn(List.of(stored));

        assertThat(agentService.get(" example-agent ")).isSameAs(stored);
    }

    @Test
    void save_requiresAccountAndCharacter_andMatchingName() {
        AgentDocument missingAccount = agent("example-agent");
        missingAccount.setAccountName(" ");
        AgentDocument other = agent("guide");

        assertThatThrownBy(() -> agentService.save("example-agent", missingAccount))
                .isInstanceOf(InvalidAgentException.class)
                .hasMessage("Agent document requires name, accountName, and characterName");
        assertThatThrownBy(() -> agentService.save("example-agent", other))
                .isInstanceOf(InvalidAgentException.class)
                .hasMessage("name does not match the path");
        verifyNoInteractions(agentRepository);
    }

    @Test
    void save_defaultsEnabled_andKeepsTheExistingId() {
        AgentDocument first = agent("example-agent");
        first.setId("66f0a1b2c3d4e5f607182930");
        AgentDocument extra = agent("EXAMPLE-AGENT");
        extra.setId("other-id");
        when(agentRepository.findAllByNameIgnoreCase("example-agent")).thenReturn(List.of(first, extra));
        AgentDocument body = agent("Example-Agent");
        body.setId("new-id");
        body.setEnabled(null);
        body.setDescription(null);
        when(agentRepository.save(body)).thenReturn(body);

        AgentDocument saved = agentService.save("example-agent", body);

        assertThat(saved.getId()).isEqualTo("66f0a1b2c3d4e5f607182930");
        assertThat(saved.getName()).isEqualTo("Example-Agent");
        assertThat(saved.getAccountName()).isEqualTo("agent-account");
        assertThat(saved.getCharacterName()).isEqualTo("Example");
        assertThat(saved.getEnabled()).isTrue();
        assertThat(saved.getDescription()).isEmpty();
        verify(agentRepository).deleteById("other-id");
    }

    @Test
    void save_usesTheBodyIdWhenTheAgentIsNew() {
        when(agentRepository.findAllByNameIgnoreCase("sleeper")).thenReturn(List.of());
        AgentDocument body = agent("sleeper");
        body.setId(" abc ");
        body.setEnabled(false);
        when(agentRepository.save(body)).thenReturn(body);

        AgentDocument saved = agentService.save("sleeper", body);

        assertThat(saved.getId()).isEqualTo("abc");
        assertThat(saved.getEnabled()).isFalse();
    }

    @Test
    void save_dataAccess_hidesTheDriverMessage() {
        when(agentRepository.findAllByNameIgnoreCase("example-agent"))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> agentService.save("example-agent", agent("example-agent")))
                .isInstanceOf(AgentPersistenceException.class)
                .hasMessage("Failed to save agent");
    }

    private static AgentDocument agent(String name) {
        AgentDocument document = new AgentDocument();
        document.setName(name);
        document.setAccountName("agent-account");
        document.setCharacterName("Example");
        document.setEnabled(true);
        document.setDescription("A guide.");
        return document;
    }
}
