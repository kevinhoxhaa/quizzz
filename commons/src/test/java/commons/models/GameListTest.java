package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        assertEquals(new HashMap<>(), gameList.getGames());
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

    @Test
    public void add(){
        Game game = new Game();
        gameList.add(game);
        assertTrue(gameList.getGames().containsValue(game));
    }

    @Test
    public void remove(){
        Game game = new Game();
        game.setGameID(0);
        gameList.add(game);
        gameList.remove(0);
        assertFalse(gameList.getGames().containsValue(game));
    }
}
