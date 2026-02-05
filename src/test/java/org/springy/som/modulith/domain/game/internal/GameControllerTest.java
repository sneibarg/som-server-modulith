package org.springy.som.modulith.domain.game.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springy.som.modulith.domain.game.api.GameDataMapper;
import org.springy.som.modulith.domain.game.api.GameDataView;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link GameController}.
 */
class GameControllerTest {

    private GameService gameService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        GameController controller = new GameController(gameService);

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void findAll_empty_returns200AndEmptyJsonArray() throws Exception {
        when(gameService.findAll()).thenReturn(emptyList());

        mvc.perform(get("/api/v1/game").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));

        verify(gameService).findAll();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void findAll_twoItems_returns200AndMappedList() throws Exception {
        GameDataDocument d1 = mock(GameDataDocument.class);
        GameDataDocument d2 = mock(GameDataDocument.class);

        // We don't rely on concrete document structure here; we just ensure mapper was invoked and JSON is an array.
        when(gameService.findAll()).thenReturn(List.of(d1, d2));

        mvc.perform(get("/api/v1/game").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(gameService).findAll();
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void findGameDataById_mapsAndReturns200Json() throws Exception {
        GameDataDocument doc = mock(GameDataDocument.class);
        when(gameService.findGameDataByRulesetId("R1")).thenReturn(doc);

        mvc.perform(get("/api/v1/game/R1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(gameService).findGameDataByRulesetId("R1");
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void findAll_invokesMapperForEachDocument() {
        // This is a pure unit test (no MVC) to assert mapper invocation count.
        // We use Mockito's inline mock maker to mock static methods; if you don't have it, see dependency note below.

        GameDataDocument d1 = mock(GameDataDocument.class);
        GameDataDocument d2 = mock(GameDataDocument.class);

        when(gameService.findAll()).thenReturn(List.of(d1, d2));

        GameController controller = new GameController(gameService);

        try (var mocked = Mockito.mockStatic(GameDataMapper.class)) {
            mocked.when(() -> GameDataMapper.toView(d1)).thenReturn(mock(GameDataView.class));
            mocked.when(() -> GameDataMapper.toView(d2)).thenReturn(mock(GameDataView.class));

            controller.findAll();

            mocked.verify(() -> GameDataMapper.toView(d1), times(1));
            mocked.verify(() -> GameDataMapper.toView(d2), times(1));
            verify(gameService).findAll();
            verifyNoMoreInteractions(gameService);
        }
    }
}
