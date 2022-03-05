package commons.entities;

import commons.models.ConsumptionQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    private static final long CONSUMPTION = 1000;

    private Game game;

    @BeforeEach
    public void startup() {
        game = new Game(new ArrayList<>(), new ConsumptionQuestion(new Activity("q1", CONSUMPTION, "src")));
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(game);
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertTrue(game.toString().contains("userIds") && game.toString().contains("nextQuestion"));
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
