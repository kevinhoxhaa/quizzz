package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {
    private static final long ID = 1L;

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
    public void getRestartUserIdsReturnsList() {
        assertEquals(new ArrayList<>(), game.getRestartUserIds());
    }

    @Test
    public void setQuestionsSetsQuestions() {
        List<Question> questions = new ArrayList<>();
        questions.add(new ConsumptionQuestion());
        game.setQuestions(questions);
        assertEquals(questions, game.getQuestions());
    }

    @Test
    public void getGameIdReturnsGameId() {
        assertEquals(0L, game.getGameID());
    }

    @Test
    public void setGameIdSetsGameId() {
        game.setGameID(ID);
        assertEquals(ID, game.getGameID());
    }

    @Test
    public void toStringContainsQuestions() {
        assertTrue(game.toString().contains("questions"));
    }

    @Test
    public void toStringContainsUserIds() {
        assertTrue(game.toString().contains("userIds"));
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
