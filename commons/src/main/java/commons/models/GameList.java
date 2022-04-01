package commons.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameList {
    private Map<Long, Game> games;

    /**
     * Constructs a new game state object
     * with an empty list of games
     */
    public GameList() {
        this.games = new HashMap();
    }

    /**
     * Returns the games in the server
     * @return the games for the current
     * game state
     */
    public Map<Long, Game> getGames() {
        return games;
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "GameState{" +
                "games=" + games +
                '}';
    }

    /**
     * Checks whether another object is of the same instance
     * and has fields equal to the given object
     * @param o the object to compare the current object with
     * @return true of the two objects are the same or are of
     * the same instance and have equal fields
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameList gameList = (GameList) o;
        return Objects.equals(games, gameList.games);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(games);
    }

    /**
     * Adds a game to the game list
     * @param game the game to add
     */
    public void add(Game game){
        games.put(game.getGameID(), game);
    }

    /**
     * Removes the game at the given index from the list
     * @param index the index of the game in the list
     * @return whether the list has been changed
     */
    public Game remove(long index){
        return games.remove(Long.valueOf(index));
    }
}
