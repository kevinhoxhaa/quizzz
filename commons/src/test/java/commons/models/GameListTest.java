package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameListTest {
    private GameList gameList;

    @BeforeEach
    public void startup() {
        gameList = new GameList();
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(gameList);
    }

    @Test
    public void getGamesReturnsList() {
        assertEquals(new ArrayList<>(), gameList.getGames());
    }

    @Test
    public void toStringReturnsValidStringRepresentation() {
        assertTrue(gameList.toString().contains("games"));
    }

    @Test
    public void equalsReturnsTrueForSameObjects() {
        assertEquals(gameList, gameList);
    }

    @Test
    public void hashCodeReturnsSameForSameObjects() {
        assertEquals(gameList.hashCode(), gameList.hashCode());
    }
}
