package commons.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameState {
    private List<Game> games;

    /**
     * Constructs a new game state object
     * with an empty list of games
     */
    public GameState() {
        this.games = new ArrayList<>();
    }

    /**
     * Returns the games in the server
     * @return the games for the current
     * game state
     */
    public List<Game> getGames() {
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
        GameState gameState = (GameState) o;
        return Objects.equals(games, gameState.games);
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
}
