package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameStateTest {
    private GameState gameState;

    @BeforeEach
    public void startup() {
        gameState = new GameState();
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(gameState);
    }

    @Test
    public void getGamesReturnsList() {
        assertEquals(new ArrayList<>(), gameState.getGames());
    }

    @Test
    public void toStringReturnsValidStringRepresentation() {
        assertTrue(gameState.toString().contains("games"));
    }

    @Test
    public void equalsReturnsTrueForSameObjects() {
        assertEquals(gameState, gameState);
    }

    @Test
    public void hashCodeReturnsSameForSameObjects() {
        assertEquals(gameState.hashCode(), gameState.hashCode());
    }
}
