package commons.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game {
    protected List<Long> userIds;
    protected List<Question> questions;
    protected List<Long> restartUserIds;
    private long gameID;

    /**
     * Constructs a new game with an empty list of user
     * ids and questions
     */
    public Game() {
        this.userIds = new ArrayList<>();
        this.questions = new ArrayList<>();
        this.restartUserIds = new ArrayList<>();
    }

    /**
     * Returns the user ids
     * @return the game user ids
     */
    public List<Long> getUserIds() {
        return userIds;
    }

    /**
     * Returns the questions in the game
     * @return the game questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Returns the user ids of the users who want to have a rematch.
     * @return The user ids of the users who want to have a rematch.
     */
    public List<Long> getRestartUserIds() {
        return restartUserIds;
    }

    /**
     * A setter for the questions
     * @param questions the list of questions
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Returns a string representation of the current
     * game
     * @return a game string representation
     */
    @Override
    public String toString() {
        return "Game{" +
                "userIds=" + userIds +
                ", questions=" + questions +
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
        Game game = (Game) o;
        return Objects.equals(userIds, game.userIds) && Objects.equals(questions, game.questions)
                && Objects.equals(restartUserIds, game.restartUserIds);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userIds, questions);
    }

    /**
     * A setter for the game id
     * @param gameID the game id to set
     */
    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    /**
     * A getter for the game id
     * @return the id of this game
     */
    public long getGameID() {
        return gameID;
    }
}
