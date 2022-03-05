package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    private Game game;

    @BeforeEach
    public void startup() {
        game = new Game();
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(game);
    }

    @Test
    public void getUserIdsReturnsList() {
        assertEquals(new ArrayList<>(), game.getUserIds());
    }

    @Test
    public void getQuestionsReturnsList() {
        assertEquals(new ArrayList<>(), game.getQuestions());
    }

    @Test
    public void toStringReturnsValidStringRepresentation() {
        assertTrue(game.toString().contains("questions") && game.toString().contains("userIds"));
    }

    @Test
    public void equalsReturnsTrueForSameObjects() {
        assertEquals(game, game);
    }

    @Test
    public void hashCodeReturnsSameForSameObjects() {
        assertEquals(game.hashCode(), game.hashCode());
    }
}
